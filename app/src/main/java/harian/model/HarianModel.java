package harian.model;

public class HarianModel {
    private int id_mod, sdhLewat;
    private String nama_petugas, tanggal, inisial;
    private int color = -1;

    public String getInisial() {
        return inisial;
    }

    public void setInisial(String inisial) {
        this.inisial = inisial;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public int getId_mod() {
        return id_mod;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setNama_petugas(String nama_petugas) {
        this.nama_petugas = nama_petugas;
    }

    public void setId_mod(int id_mod) {
        this.id_mod = id_mod;
    }

    public int getSdhLewat() {
        return sdhLewat;
    }

    public void setSdhLewat(int sdhLewat) {
        this.sdhLewat = sdhLewat;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}