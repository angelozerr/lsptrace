package com.redhat.lsptrace;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.ColorInformation;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentColorParams;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.DocumentLinkParams;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.FoldingRangeRequestParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.LinkedEditingRangeParams;
import org.eclipse.lsp4j.LinkedEditingRanges;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.lsptrace.process.StreamConnectionProvider;
import com.redhat.lsptrace.trace.LSPPayload;
import com.redhat.lsptrace.trace.LSPTraceLoader;

public class LSPTraceTester {

	private static final Gson gson = new GsonBuilder() //
			.setPrettyPrinting() //
			.registerTypeAdapterFactory(new EitherTypeAdapter.Factory()).create();

	private final StreamConnectionProvider provider;

	public LSPTraceTester(StreamConnectionProvider provider) {
		this.provider = provider;
	}

	public void process(File traceFile) throws IOException {
		process(new FileReader(traceFile));
	}

	public void process(Reader traceFile) throws IOException {
		LSPTraceLoader loader = new LSPTraceLoader();
		List<LSPPayload> messages = loader.load(traceFile);

		process(messages);
	}

	private void process(List<LSPPayload> messages) throws IOException {

		provider.start();
		
		LanguageClient client = new MockLanguageClient();
		Launcher<LanguageServer> launcher = LSPLauncher.createClientLauncher(client, provider.getInputStream(),
				provider.getOutputStream());
		LanguageServer languageServer = launcher.getRemoteProxy();
		Future<Void> launcherFuture = launcher.startListening();

		try {
			process(messages, languageServer);
		} finally {			
			try {
				languageServer.shutdown().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			launcherFuture.cancel(true);
			languageServer.exit();
			provider.stop();
		}
	}

	private void process(List<LSPPayload> messages, LanguageServer languageServer) {

		for (LSPPayload message : messages) {
			try {
				switch (message.getMethod()) {

				case "initialize": {
					InitializeParams params = gson.fromJson(message.getBody(), InitializeParams.class);
					languageServer.initialize(params);
					break;
				}
				case "initialized": {

					break;
				}
				case "workspace/didChangeConfiguration": {

					break;
				}
				case "xml/closeTag": {

					break;
				}
				case "textDocument/didOpen": {
					DidOpenTextDocumentParams params = gson.fromJson(message.getBody(),
							DidOpenTextDocumentParams.class);
					languageServer.getTextDocumentService().didOpen(params);
					break;
				}
				case "textDocument/didChange": {
					DidChangeTextDocumentParams params = gson.fromJson(message.getBody(),
							DidChangeTextDocumentParams.class);
					languageServer.getTextDocumentService().didChange(params);
					break;
				}
				case "textDocument/didClose": {
					DidCloseTextDocumentParams params = gson.fromJson(message.getBody(),
							DidCloseTextDocumentParams.class);
					languageServer.getTextDocumentService().didClose(params);
					break;
				}
				case "textDocument/didSave": {
					DidSaveTextDocumentParams params = gson.fromJson(message.getBody(),
							DidSaveTextDocumentParams.class);
					languageServer.getTextDocumentService().didSave(params);
					break;
				}
				case "textDocument/documentLink": {
					DocumentLinkParams params = gson.fromJson(message.getBody(), DocumentLinkParams.class);
					CompletableFuture<List<DocumentLink>> result = languageServer.getTextDocumentService()
							.documentLink(params);
					System.err.println();
					System.err.println("textDocument/documentLink --> ");
					System.err.println(gson.toJson(result.get(), List.class));
					break;
				}
				case "textDocument/codeAction": {
					CodeActionParams params = gson.fromJson(message.getBody(), CodeActionParams.class);
					CompletableFuture<List<Either<Command, CodeAction>>> result = languageServer
							.getTextDocumentService().codeAction(params);
					System.err.println();
					System.err.println("textDocument/codeAction --> ");
					System.err.println(gson.toJson(result.get(), List.class));
					break;
				}
				case "codeAction/resolve": {
					CodeAction params = gson.fromJson(message.getBody(), CodeAction.class);
					CompletableFuture<CodeAction> result = languageServer.getTextDocumentService()
							.resolveCodeAction(params);
					System.err.println();
					System.err.println("textDocument/codeAction --> ");
					System.err.println(gson.toJson(result.get(), CodeAction.class));
					break;
				}
				case "textDocument/codeLens": {
					CodeLensParams params = gson.fromJson(message.getBody(), CodeLensParams.class);
					CompletableFuture<List<? extends CodeLens>> result = languageServer.getTextDocumentService()
							.codeLens(params);
					System.err.println();
					System.err.println("textDocument/codeLens --> ");
					System.err.println(gson.toJson(result.get(), List.class));
					break;
				}
				case "textDocument/completion": {
					CompletionParams params = gson.fromJson(message.getBody(), CompletionParams.class);
					CompletableFuture<Either<List<CompletionItem>, CompletionList>> result = languageServer
							.getTextDocumentService().completion(params);
					Either<List<CompletionItem>, CompletionList> completion = result.get();
					System.err.println();
					System.err.println("textDocument/completion --> ");
					System.err.println(
							completion != null ? gson.toJson(completion.getRight(), CompletionList.class) : null);
					break;
				}
				case "completionItem/resolve": {
					CompletionItem params = gson.fromJson(message.getBody(), CompletionItem.class);
					CompletableFuture<CompletionItem> result = languageServer.getTextDocumentService()
							.resolveCompletionItem(params);
					System.err.println();
					System.err.println("completionItem/resolve --> ");
					System.err.println(gson.toJson(result.get(), CompletionItem.class));
					break;
				}
				case "textDocument/foldingRange": {
					FoldingRangeRequestParams params = gson.fromJson(message.getBody(),
							FoldingRangeRequestParams.class);
					CompletableFuture<List<FoldingRange>> result = languageServer.getTextDocumentService()
							.foldingRange(params);
					System.err.println();
					System.err.println("textDocument/foldingRange --> ");
					System.err.println(gson.toJson(result.get(), List.class));
					break;
				}
				case "textDocument/documentColor": {
					DocumentColorParams params = gson.fromJson(message.getBody(), DocumentColorParams.class);
					CompletableFuture<List<ColorInformation>> result = languageServer.getTextDocumentService()
							.documentColor(params);
					Object[] array = result.get().toArray();
					System.err.println();
					System.err.println("textDocument/documentColor --> ");
					System.err.println(gson.toJson(array, ColorInformation[].class));
					break;
				}
				case "textDocument/hover": {
					HoverParams params = gson.fromJson(message.getBody(), HoverParams.class);
					CompletableFuture<Hover> result = languageServer.getTextDocumentService().hover(params);
					System.err.println();
					System.err.println("textDocument/hover --> ");
					System.err.println(gson.toJson(result.get(), Hover.class));
					break;
				}
				case "textDocument/linkedEditingRange": {
					LinkedEditingRangeParams params = gson.fromJson(message.getBody(), LinkedEditingRangeParams.class);
					CompletableFuture<LinkedEditingRanges> result = languageServer.getTextDocumentService()
							.linkedEditingRange(params);
					System.err.println();
					System.err.println("textDocument/linkedEditingRange --> ");
					System.err.println(gson.toJson(result.get(), LinkedEditingRanges.class));
					break;
				}
				default:
					System.err.println("argh");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
