package net.murasakiyamaimo.Lisatoprogram;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.ProgramNode;
import net.murasakiyamaimo.Lisatoprogram.compiler.codegen.JavaCodeGenerator;
import net.murasakiyamaimo.Lisatoprogram.compiler.semantics.SemanticAnalyzer;
import net.murasakiyamaimo.Lisatoprogram.compiler.semantics.SemanticException;
import net.murasakiyamaimo.Lisatoprogram.astbuilder.AstBuilder;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramLexer;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramParser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        String filePathString = "C:\\Users\\kunik\\IdeaProjects\\Lisatoprogram\\src\\main\\resources\\program.txt";
        String sourceCode = "";

        try {
            Path filePath = Paths.get(filePathString);
            sourceCode = Files.readString(filePath);

        } catch (IOException e) {
            System.err.println("ファイルの読み込み中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- あなたの言語のソースコード ---");
        System.out.println(sourceCode);
        System.out.println("-------------------------------------\n");

        try {
            // 1. 字句解析と構文解析 (ANTLRを使用)
            CharStream input = CharStreams.fromString(sourceCode);
            LisatoprogramLexer lexer = new LisatoprogramLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LisatoprogramParser parser = new LisatoprogramParser(tokens);

            // エラーリスナーを追加して、構文エラーを捕捉
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new RuntimeException("構文エラー (行 " + line + ", 位置 " + charPositionInLine + "): " + msg);
                }
            });

            ParseTree tree = parser.program(); // プログラムのルートルールを解析

            // 2. カスタムVisitorでASTを構築
            AstBuilder astBuilder = new AstBuilder();
            ProgramNode programAst = (ProgramNode) astBuilder.visit(tree);

            // 3. 意味解析
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(programAst); // AST全体を解析

            // 4. Javaコード生成
            JavaCodeGenerator codeGenerator = new JavaCodeGenerator(semanticAnalyzer.getSymbolTable());
            String generatedJavaCode = codeGenerator.generate(programAst);

            String fullJavaProgram = codeGenerator.getFullJavaProgram(generatedJavaCode);

            System.out.println("--- 生成されたJavaコード ---");
            System.out.println(fullJavaProgram);
            System.out.println("-------------------------------------\n");

            // 5. Javaコードのコンパイルと実行
            System.out.println("--- 生成されたJavaコードの実行結果 ---");
            compileAndRun(fullJavaProgram, "Main");
            System.out.println("-------------------------------------\n");

        } catch (SemanticException e) {
            System.err.println("意味解析エラー: " + e.getMessage());
        } catch (RuntimeException e) { // 構文エラーや未サポートの型など、その他の実行時エラー
            System.err.println("コンパイル/実行エラー: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("予期せぬエラー: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 生成されたJavaコードをコンパイルし、実行するユーティリティメソッド
    private static void compileAndRun(String javaCode, String className) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("JDKがインストールされていません。JREではコンパイルできません。");
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("my_compiler_temp");
            Path sourceFile = tempDir.resolve(className + ".java");
            Files.writeString(sourceFile, javaCode, StandardCharsets.UTF_8);

            String[] compileArgs = {
                    sourceFile.toString(),
                    "-d", tempDir.toString() // コンパイルされたクラスファイルを一時ディレクトリに出力
            };

            // コンパイルエラーを捕捉するためのByteArrayOutputStream
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            int compileResult = compiler.run(null, null, new PrintStream(err), compileArgs);

            if (compileResult != 0) {
                throw new RuntimeException("生成されたJavaコードのコンパイルに失敗しました:\n" + err.toString());
            }

            // 実行
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { tempDir.toUri().toURL() });
            Class<?> compiledClass = Class.forName(className, true, classLoader);
            Method mainMethod = compiledClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]); // mainメソッドを実行

        } finally {
            // 一時ディレクトリをクリーンアップ
            if (tempDir != null && Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }
}