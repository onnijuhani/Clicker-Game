package model.characters.ai.actions;

import model.characters.Person;
import model.characters.Trait;
import model.time.Time;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class NPCActionLogger {
    private static final int MAX_LOG_ENTRIES = 10000;
    private final Queue<String> logs = new LinkedList<>();

    /**
     * @param npc NPC Person that executed the method
     * @param action Class that the method belongs to. Should always be "this"
     * @param trait Type of the method, slaver ambitious etc
     * @param details The actual action and relevant information
     */
    public synchronized void logAction(Person npc, NPCAction action, Trait trait, String details) {
        if (logs.size() >= MAX_LOG_ENTRIES) {
            logs.poll();
        }
        String logEntry = String.format("%s: [%s] [%s] %s-Action %s - %s", LocalDateTime.now(), npc.getName(), npc.getRole(), trait ,action.getClass().getSimpleName(), details);
        logs.add(logEntry);
    }

    public synchronized void logAction(Person npc, NPCAction action, String trait, String details) {
        if (logs.size() >= MAX_LOG_ENTRIES) {
            logs.poll();
        }
        String logEntry = String.format("%s: [%s] [%s] [%s-Action] %s | %s | [%s]",
                Time.getClock(), npc.getName(), npc.getRole(), trait ,action.getClass().getSimpleName(), details, npc.getWallet().toShortString());
        logs.add(logEntry);
    }

    public synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public synchronized List<String> getLogsForNPC(String npcName) {
        return logs.stream()
                .filter(log -> log.contains(npcName))
                .collect(Collectors.toList());
    }
}
