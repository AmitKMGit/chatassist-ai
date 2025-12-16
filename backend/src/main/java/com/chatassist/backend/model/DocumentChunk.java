package com.chatassist.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DocumentChunk {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Document document;

    @Column(length = 2000)
    private String content;

    @Column(length = 8000)
    private String embedding; // JSON string

    private int chunkIndex;
    // getters & setters
    public Long getId() { return id; }
    public Document getDocument() { return document; }
    public String getContent() { return content; }
    public String getEmbedding() { return embedding; }
    public int getChunkIndex() { return chunkIndex; }

    public void setDocument(Document document) { this.document = document; }
    public void setContent(String content) { this.content = content; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }

}
