package com.ano;

/**
 * Created by ano on 02.10.16.
 */
public class Mail {
    private String from;
    private String text;
    private int count;
    private boolean seen;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Mail(String from, String text, int count, boolean seen) {
        this.from = from;
        this.text = text;
        this.count=count;
        this.seen=seen;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
