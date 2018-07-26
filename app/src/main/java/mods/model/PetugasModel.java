package mods.model;

public class PetugasModel {
    private int id_aphris;
    private String nama_petugas, inisial;
    private int color=-1;

    public int getId_aphris() {
        return id_aphris;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public void setId_aphris(int id_aphris) {
        this.id_aphris = id_aphris;
    }

    public void setNama_petugas(String nama_petugas) {
        this.nama_petugas = nama_petugas;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setInisial(String inisial) {
        this.inisial = inisial;
    }

    public String getInisial() {
        return inisial;
    }
}
