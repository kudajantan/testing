package menus.model;

public class MenusModel {
    private int id_menu;
    private String icon_menu, nama_menu;

    public int getId_menu() {
        return id_menu;
    }

    public String getIcon_menu() {
        return icon_menu;
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public void setIcon_menu(String icon_menu) {
        this.icon_menu = icon_menu;
    }

    public void setId_menu(int id_menu) {
        this.id_menu = id_menu;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }


}