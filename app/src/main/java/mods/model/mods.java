package mods.model;

public class mods {
    private int id_mod, shift, sdhLewat, level;
    private String nama_petugas, tanggal, inisial;
    private int color = -1;

    public void setId_mod(int id_mod) {
        this.id_mod = id_mod;
    }

    public void setNama_petugas(String nama_petugas) {
        this.nama_petugas = nama_petugas;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void setSdhLewat(int sdhLewat) {
        this.sdhLewat = sdhLewat;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getId_mod() {
        return id_mod;
    }

    public int getSdhLewat() {
        return sdhLewat;
    }

    public int getShift() {
        return shift;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getInisial() {
        return inisial;
    }

    public void setInisial(String inisial) {
        this.inisial = inisial;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
