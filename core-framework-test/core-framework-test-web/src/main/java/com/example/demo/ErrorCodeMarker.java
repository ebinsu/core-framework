package com.example.demo;

import org.slf4j.Marker;

import java.util.Iterator;

/**
 * @author ebin
 */
public class ErrorCodeMarker implements Marker {
    String code;

    public ErrorCodeMarker(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return code;
    }

    @Override
    public void add(Marker marker) {

    }

    @Override
    public boolean remove(Marker marker) {
        return false;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasReferences() {
        return false;
    }

    @Override
    public Iterator<Marker> iterator() {
        return null;
    }

    @Override
    public boolean contains(Marker marker) {
        return false;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }
}
