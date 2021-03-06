package server.commands;

import general.request.Request;
import general.response.Response;
import server.application.ServerApplication;
import server.collection.CollectionManager;
import server.file.FilesWriter;
import java.util.*;

public class ServerCommandManager {
    private Map<String, ServerCommand> commandsOfClient;
    private Map<String, ServerCommand> commandsOfServer;
    private List<String> commandsOfClientHistory = new ArrayList<>();
    private final int LIMIT = 9;

    public ServerCommandManager(ServerApplication application, CollectionManager collectionManager, FilesWriter filesWriter) {
        commandsOfClient = new HashMap<>();
        commandsOfClient.put("help", new HelpCommand(commandsOfClient.keySet(), commandsOfClient));
        commandsOfClient.put("info", new InfoCommand(collectionManager));
        commandsOfClient.put("show", new ShowCommand(collectionManager));
        commandsOfClient.put("insert", new InsertCommand(collectionManager));
        commandsOfClient.put("update", new UpdateCommand(collectionManager));
        commandsOfClient.put("remove_key", new RemoveKeyCommand(collectionManager));
        commandsOfClient.put("clear", new ClearCommand(collectionManager));
        commandsOfClient.put("remove_greater", new RemoveGreaterCommand(collectionManager));
        commandsOfClient.put("remove_lower", new RemoveLowerCommand(collectionManager));
        commandsOfClient.put("history", new HistoryCommand(commandsOfClientHistory, LIMIT));
        commandsOfClient.put("max_by_golden_palm_count", new MaxByGoldenPalmCountCommand(collectionManager));
        commandsOfClient.put("print_ascending", new PrintAscendingCommand(collectionManager));
        commandsOfClient.put("print_field_ascending_golden_palm_count", new PrintFieldAscendingGoldenPalmCountCommand(collectionManager));

        commandsOfServer = new HashMap<>();
        commandsOfServer.put("shutdown", new ShutdownCommand(application));
        commandsOfServer.put("save", new SaveCommand(filesWriter));
    }

    public Response manageRequestOfClient(Request request) {
        if (request != null) {
            String command = request.getCommand();
            if (commandsOfClient.containsKey(command)) {
                if (commandsOfClientHistory.size() < LIMIT)
                    commandsOfClientHistory.add(command);
                else {
                    commandsOfClientHistory.remove(0);
                    commandsOfClientHistory.add(command);
                }
                try {
                    ServerInvoker invoker = new ServerInvoker(commandsOfClient.get(command));
                    return invoker.executeCommand(request);
                } catch (NumberFormatException e) {
                    return new Response(null, "???????????????? ?????? ??????????????????!");
                }
            }
            else
                return new Response(null, "???????????????????????? ??????????????. ???????????????? help ?????? ??????????????.");
        }
        return null;
    }
    
    public void manageRequestOfServer (Request request) {
        String command = request.getCommand();
        if (commandsOfServer.containsKey(command)) {
            ServerInvoker invoker = new ServerInvoker(commandsOfServer.get(command));
            invoker.executeCommand(request);
        }
    }
}