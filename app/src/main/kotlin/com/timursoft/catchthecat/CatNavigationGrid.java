package com.timursoft.catchthecat;

import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.PathFinderOptions;
import org.xguzm.pathfinding.grid.NavigationGridGraph;
import org.xguzm.pathfinding.grid.NavigationGridGraphNode;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import java.util.ArrayList;
import java.util.List;

public class CatNavigationGrid implements NavigationGridGraph<Cell> {
    protected int width;
    protected int height;
    private List<Cell> neighbors = new ArrayList<>();

    protected Cell[][] nodes;

    public CatNavigationGrid(Cell[][] nodes) {
        setNodes(nodes);
    }

    @Override
    public Cell getCell(int x, int y) {
        return this.contains(x, y) ? this.nodes[x][y] : null;
    }

    @Override
    public void setCell(int x, int y, Cell cell) {
        if (this.contains(x, y))
            nodes[x][y] = cell;
    }

    /**
     * Determine whether the node at the given position is walkable.
     *
     * @param x - The x / column coordinate of the node.
     * @param y - The y / row coordinate of the node.
     * @return true if the node at [x,y] is walkable, false if it is not walkable (or if [x,y] is not within the grid's limit)
     */
    public boolean isWalkable(int x, int y) {
        return this.contains(x, y) && this.nodes[x][y].isWalkable();
    }

    /**
     * Determine wether the given x,y pair is within the bounds of this grid
     *
     * @param x - The x / column coordinate of the node.
     * @param y - The y / row coordinate of the node.
     * @return true if the (x,y) is within the boundaries of this grid
     */
    public boolean contains(int x, int y) {
        return (x >= 0 && x < this.width) && (y >= 0 && y < this.height);
    }

    ;


    /**
     * Set whether the node on the given position is walkable.
     *
     * @param x        - The x / column coordinate of the node.
     * @param y        - The y / row coordinate of the node.
     * @param walkable - Whether the position is walkable.
     * @throws IndexOutOfBoundsException if the coordinate is not inside the grid.
     */
    public void setWalkable(int x, int y, boolean walkable) {
        this.nodes[x][y].setWalkable(walkable);
    }

    ;


    @Override
    public List<Cell> getNeighbors(Cell cell) {
        return null;
    }

    @Override
    public List<Cell> getNeighbors(Cell node, PathFinderOptions opt) {
        GridFinderOptions options = (GridFinderOptions) opt;
        boolean even = node.getEven();
        int yDir = options.isYDown ? -1 : 1;
        int x = node.getX(), y = node.getY();
        neighbors.clear();

        // up
        if (isWalkable(x, y + yDir)) {
            neighbors.add(nodes[x][y + yDir]);
        }
        // right
        if (isWalkable(x + 1, y)) {
            neighbors.add(nodes[x + 1][y]);
        }
        // down
        if (isWalkable(x, y - yDir)) {
            neighbors.add(nodes[x][y - yDir]);
        }
        // left
        if (isWalkable(x - 1, y)) {
            neighbors.add(nodes[x - 1][y]);
        }

        // up left
        if (even && isWalkable(x - 1, y + yDir)) {
            neighbors.add(nodes[x - 1][y + yDir]);
        }
        // up right
        if (!even && isWalkable(x + 1, y + yDir)) {
            neighbors.add(nodes[x + 1][y + yDir]);
        }
        // down right
        if (!even && isWalkable(x + 1, y - yDir)) {
            neighbors.add(nodes[x + 1][y - yDir]);
        }
        // down left
        if (even && isWalkable(x - 1, y - yDir)) {
            neighbors.add(nodes[x - 1][y - yDir]);
        }

        return neighbors;
    }

    @Override
    public float getMovementCost(Cell node1, Cell node2, PathFinderOptions opt) {
        if (node1 == node2)
            return 0;

        GridFinderOptions options = (GridFinderOptions) opt;
        return node1.x == node2.x || node1.y == node2.y ?
                options.orthogonalMovementCost : options.diagonalMovementCost;
    }

    @Override
    public boolean isWalkable(Cell node) {
        return isWalkable(node.x, node.y);
    }

    public Cell[][] getNodes() {
        return nodes;
    }

    public void setNodes(Cell[][] nodes) {
        if (nodes != null) {
            this.width = nodes.length;
            this.height = nodes[0].length;
        } else {
            this.width = 0;
            this.height = 0;
        }

        this.nodes = nodes;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean lineOfSight(NavigationNode from, NavigationNode to) {
        if (from == null || to == null)
            return false;

        NavigationGridGraphNode node = (NavigationGridGraphNode) from, neigh = (NavigationGridGraphNode) to;
        int x1 = node.getX(), y1 = node.getY();
        int x2 = neigh.getX(), y2 = neigh.getY();
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        int xinc = (x1 < x2) ? 1 : -1;
        int yinc = (y1 < y2) ? 1 : -1;

        int error = dx - dy;

        for (int n = dx + dy; n > 0; n--) {
            if (!isWalkable(x1, y1))
                return false;
            int e2 = 2 * error;
            if (e2 > -dy) {
                error -= dy;
                x1 += xinc;
            }
            if (e2 < dx) {
                error += dx;
                y1 += yinc;
            }
        }

        return true;

    }
}
