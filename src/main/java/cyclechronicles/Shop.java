package cyclechronicles;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

/**
 * Eine kleine Fahrradwerkstatt mit Auftragsannahme, Reparatur und Auslieferung.
 * Jetzt erweitert um Logging aller Zustandsänderungen.
 */
public class Shop {
    private final Queue<Order> pendingOrders = new LinkedList<>();
    private final Set<Order> completedOrders = new HashSet<>();

    // Logger zum Protokollieren von Zustandsänderungen
    private static final Logger logger = Logger.getLogger(Shop.class.getName());

    // Logger wird einmalig konfiguriert: schreibt CSV-Datei
    static {
        try {
            FileHandler handler = new FileHandler("shop-log.csv", true);
            handler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format("%s,%s,%s,%s,%s,%s\n",
                        new Date(record.getMillis()).toInstant(),
                        record.getLevel(),
                        record.getSourceMethodName(),
                        record.getSourceClassName(),
                        record.getMessage(),
                        record.getParameters() != null ? record.getParameters()[0] : "-");
                }
            });
            logger.addHandler(handler);
            logger.setUseParentHandlers(false); // Keine doppelte Ausgabe auf Konsole
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Nimmt einen Reparaturauftrag an, wenn alle Bedingungen erfüllt sind.
     * Bedingungen:
     * - Kein Gravel- oder E-Bike
     * - Max. 1 Auftrag pro Kunde
     * - Max. 5 Aufträge gleichzeitig
     *
     * Wird der Auftrag angenommen, wird dies ins Log geschrieben.
     */
    public boolean accept(Order o) {
        if (o.getBicycleType() == Type.GRAVEL) return false;
        if (o.getBicycleType() == Type.EBIKE) return false;
        if (pendingOrders.stream().anyMatch(x -> x.getCustomer().equals(o.getCustomer())))
            return false;
        if (pendingOrders.size() > 4) return false;

        boolean added = pendingOrders.add(o);
        if (added) {
            logChange("accept", o, "pendingOrders");
        }
        return added;
    }

    /**
     * Repariert den ältesten Auftrag und verschiebt ihn in die Liste der erledigten Aufträge.
     * Beide Änderungen (Entfernen aus Warteschlange + Hinzufügen zur erledigten Liste) werden geloggt.
     */
    public Optional<Order> repair() {
        if (pendingOrders.isEmpty()) return Optional.empty();
        Order o = pendingOrders.poll();
        completedOrders.add(o);

        logChange("repair", o, "pendingOrders");
        logChange("repair", o, "completedOrders");
        return Optional.of(o);
    }

    /**
     * Liefert einen Auftrag an einen bestimmten Kunden aus (falls vorhanden).
     * Erfolgreiche Auslieferung wird ins Log geschrieben.
     */
    public Optional<Order> deliver(String c) {
        for (Order o : completedOrders) {
            if (o.getCustomer().equals(c)) {
                completedOrders.remove(o);
                logChange("deliver", o, "completedOrders");
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    /**
     * Hilfsmethode: schreibt eine Zeile in die Log-Datei, wenn ein Auftrag in einer Struktur verändert wurde.
     *
     * @param method     Methode, die das ausgelöst hat (z. B. "accept")
     * @param o          betroffener Auftrag
     * @param structure  betroffene Datenstruktur (z. B. "pendingOrders")
     */
    private void logChange(String method, Order o, String structure) {
        logger.logp(Level.INFO, Shop.class.getName(), method,
            String.format("Typ=%s,Kunde=%s", o.getBicycleType(), o.getCustomer()),
            new Object[]{structure});
    }

    /**
     * Ermöglicht das Setzen des Log-Levels von außen (z. B. im Test).
     * Beispiel: Shop.setLogLevel(Level.OFF);
     */
    public static void setLogLevel(Level level) {
        logger.setLevel(level);
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
        }
    }
}
