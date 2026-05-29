package com.digimontcg;

import com.google.gson.annotations.SerializedName;

public class Carta {
    @SerializedName(value = "codigo", alternate = {"cardnumber", "id"})
    private String codigo;
    
    private String name;
    private String color;
    private int level;
    
    @SerializedName(value = "imagemUrl", alternate = {"pretty_url", "image_url"})
    private String imagemUrl;
    
    private int quantidade;

    // --- GETTERS E SETTERS ---

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    // --- SMART GETTER ULTRA ROBUSTO ---
    public String getImagemUrl() {
        // Se a API mandou uma URL direta e válida, usamos ela
        if (imagemUrl != null && !imagemUrl.isEmpty() && imagemUrl.startsWith("http")) {
            return imagemUrl;
        }
        
        // Se não, limpamos e higienizamos o código para montar o link perfeito
        if (codigo != null && !codigo.isEmpty()) {
            String codigoLimpo = codigo.trim().toUpperCase();
            
            // Remove extensões duplicadas que a API possa ter injetado no ID
            if (codigoLimpo.endsWith(".JPG")) {
                codigoLimpo = codigoLimpo.substring(0, codigoLimpo.length() - 4);
            }
            if (codigoLimpo.endsWith(".PNG")) {
                codigoLimpo = codigoLimpo.substring(0, codigoLimpo.length() - 4);
            }
            
            return "https://images.digimoncard.io/images/cards/" + codigoLimpo + ".jpg";
        }
        
        return null;
    }

    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}