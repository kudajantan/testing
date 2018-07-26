package defect.model;

/**
 * Created by Rius on 6/13/2018.
 */

public class DefectModel {
    private int id_defect, level_user;
    private String tanggal, due_date, tgl_selesai, nama_defect, status_defect, gambar, ket_defect, pic, nama_lengkap, lokasi;
    private String resiko;


    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getId_defect() {
        return id_defect;
    }

    public String getDue_date() {
        return due_date;
    }

    public String getNama_defect() {
        return nama_defect;
    }

    public void setId_defect(int id_defect) {
        this.id_defect = id_defect;
    }

    public String getStatus_defect() {
        return status_defect;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public void setNama_defect(String nama_defect) {
        this.nama_defect = nama_defect;
    }

    public void setStatus_defect(String status_defect) {
        this.status_defect = status_defect;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getGambar() {
        return gambar;
    }

    public String getKet_defect() {
        return ket_defect;
    }

    public void setKet_defect(String ket_defect) {
        this.ket_defect = ket_defect;
    }

    public String getPic() {
        return pic;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public int getLevel_user() {
        return level_user;
    }

    public void setLevel_user(int level_user) {
        this.level_user = level_user;
    }

    public String getResiko() {
        return resiko;
    }

    public void setResiko(String resiko) {
        this.resiko = resiko;
    }

    public String getTgl_selesai() {
        return tgl_selesai;
    }

    public void setTgl_selesai(String tgl_selesai) {
        this.tgl_selesai = tgl_selesai;
    }
}
