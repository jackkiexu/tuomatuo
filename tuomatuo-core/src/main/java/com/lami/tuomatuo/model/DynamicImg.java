package com.lami.tuomatuo.model;

import javax.persistence.*;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Entity
@Table(name = "dynamicimg")
public class DynamicImg  implements java.io.Serializable {

    private Long id;
    private Long userId;
    private Integer height;
    private Integer width;
    private Integer quantity;
    private String url;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DynamicImg{" +
                "id=" + id +
                ", userId=" + userId +
                ", height=" + height +
                ", width=" + width +
                ", quantity=" + quantity +
                ", url='" + url + '\'' +
                '}';
    }
}
