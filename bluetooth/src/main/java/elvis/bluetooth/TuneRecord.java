package elvis.bluetooth;

/**
 * Created by elvis on 2018/4/2.
 */

public class TuneRecord {
    String notes;
    String name;
    int id;

    TuneRecord(String notes, String names) {
        this.notes = notes;
        this.name = names;
    }
    TuneRecord(String notes, String names, int id) {
        this.notes = notes;
        this.name = names;
        this.id = id;
    }
}