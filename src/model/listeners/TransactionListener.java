package model.listeners;

import model.units.Node;

import java.awt.geom.Point2D;

public interface TransactionListener {
    void onCommitted(Node node, Point2D finalPosition);
}
