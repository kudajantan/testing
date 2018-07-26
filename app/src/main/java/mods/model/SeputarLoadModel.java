package mods.model;

public class SeputarLoadModel {
    private int id_mod, id_seputar, id_area, id_lokasi, id_status, id_pic;
    private String ket, area, lokasi, status, pic, gambar, nama_defect, resiko, caption;

    public void setId_seputar(int id_seputar) {
        this.id_seputar = id_seputar;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getId_seputar() {
        return id_seputar;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKet() {
        return ket;
    }

    public String getGambar() {
        return gambar;
    }

    public int getId_mod() {
        return id_mod;
    }

    public String getArea() {
        return area;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getPic() {
        return pic;
    }

    public String getStatus() {
        return status;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setId_mod(int id_mod) {
        this.id_mod = id_mod;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama_defect() {
        return nama_defect;
    }

    public String getResiko() {
        return resiko;
    }

    public void setNama_defect(String nama_defect) {
        this.nama_defect = nama_defect;
    }

    public void setResiko(String resiko) {
        this.resiko = resiko;
    }

    public void setId_area(int id_area) {
        this.id_area = id_area;
    }

    public int getId_area() {
        return id_area;
    }

    public void setId_pic(int id_pic) {
        this.id_pic = id_pic;
    }

    public int getId_pic() {
        return id_pic;
    }

    public int getId_status() {
        return id_status;
    }

    public void setId_status(int id_status) {
        this.id_status = id_status;
    }

    public void setId_lokasi(int id_lokasi) {
        this.id_lokasi = id_lokasi;
    }

    public int getId_lokasi() {
        return id_lokasi;
    }

}