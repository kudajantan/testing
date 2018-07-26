package defect.model;

public class DefectViewModel {
    private String tanggal, gambar, ket;

    public String getGambar() {
        return gambar;
    }

    public String getKet() {
        return ket;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }


}