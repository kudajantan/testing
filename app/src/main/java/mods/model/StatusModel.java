package mods.model;


public class StatusModel {
    private int id_status;
    private String nama_status;


    public void setNama_status(String nama_status) {
        this.nama_status = nama_status;
    }

    public void setId_status(int id_status) {
        this.id_status = id_status;
    }

    public String getNama_status() {
        return nama_status;
    }

    public int getId_status() {
        return id_status;
    }

}
