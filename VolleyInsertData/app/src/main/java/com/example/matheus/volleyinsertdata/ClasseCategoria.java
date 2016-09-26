package com.example.matheus.volleyinsertdata;

import java.io.Serializable;

/**
 * Created by Matheus on 17/09/2016.
 */
    public class ClasseCategoria implements Serializable {

        private long id;
        private String desc_categoria, img_categoria;



    public ClasseCategoria(long id, String desc_categoria, String img_categoria) {
            this.id = id;
            this.desc_categoria = desc_categoria;
            this.img_categoria = img_categoria;

        }

    public ClasseCategoria() {

    }

    public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getDesc_categoria() {
            return desc_categoria;
        }

        public void setDesc_categoria(String desc_categoria) {
            this.desc_categoria = desc_categoria;
        }

        public String getImg_categoria() {
            return img_categoria;
        }

        public void setImg_categoria(String img_categoria) {
            this.img_categoria = img_categoria;
        }
}
