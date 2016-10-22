package com.timursoft.catchthecat

import org.xguzm.pathfinding.NavigationNode
import org.xguzm.pathfinding.PathFinderOptions
import org.xguzm.pathfinding.grid.NavigationGridGraph
import org.xguzm.pathfinding.grid.NavigationGridGraphNode
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import java.util.*

class CatNavigationGrid(nodes: Array<Array<Cell?>>) : NavigationGridGraph<Cell> {

    private var width = 0
    private var height = 0
    private val neighbors = ArrayList<Cell>()

    private lateinit var nodes: Array<Array<Cell?>>

    init {
        setNodes(nodes)
    }

    override fun getCell(x: Int, y: Int): Cell? {
        return if (this.contains(x, y)) this.nodes[x][y] else null
    }

    override fun setCell(x: Int, y: Int, cell: Cell) {
        if (this.contains(x, y))
            nodes[x][y] = cell
    }

    /**
     * Determine whether the node at the given position is walkable.

     * @param x - The x / column coordinate of the node.
     * *
     * @param y - The y / row coordinate of the node.
     * *
     * @return true if the node at [x,y] is walkable, false if it is not walkable (or if [x,y] is not within the grid's limit)
     */
    override fun isWalkable(x: Int, y: Int): Boolean {
        return this.contains(x, y) && this.nodes[x][y]!!.isWalkable
    }

    /**
     * Determine wether the given x,y pair is within the bounds of this grid

     * @param x - The x / column coordinate of the node.
     * *
     * @param y - The y / row coordinate of the node.
     * *
     * @return true if the (x,y) is within the boundaries of this grid
     */
    override fun contains(x: Int, y: Int): Boolean {
        return x >= 0 && x < this.width && y >= 0 && y < this.height
    }


    /**
     * Set whether the node on the given position is walkable.

     * @param x        - The x / column coordinate of the node.
     * *
     * @param y        - The y / row coordinate of the node.
     * *
     * @param walkable - Whether the position is walkable.
     * *
     * @throws IndexOutOfBoundsException if the coordinate is not inside the grid.
     */
    override fun setWalkable(x: Int, y: Int, walkable: Boolean) {
        this.nodes[x][y]!!.isWalkable = walkable
    }


    override fun getNeighbors(cell: Cell): List<Cell>? {
        return null
    }

    override fun getNeighbors(node: Cell, opt: PathFinderOptions): List<Cell> {
        val options = opt as GridFinderOptions
        val even = node.even
        val yDir = if (options.isYDown) -1 else 1
        val x = node.getX()
        val y = node.getY()
        neighbors.clear()

        // up
        if (isWalkable(x, y + yDir)) {
            neighbors.add(nodes[x][y + yDir]!!)
        }
        // right
        if (isWalkable(x + 1, y)) {
            neighbors.add(nodes[x + 1][y]!!)
        }
        // down
        if (isWalkable(x, y - yDir)) {
            neighbors.add(nodes[x][y - yDir]!!)
        }
        // left
        if (isWalkable(x - 1, y)) {
            neighbors.add(nodes[x - 1][y]!!)
        }

        // up left
        if (even && isWalkable(x - 1, y + yDir)) {
            neighbors.add(nodes[x - 1][y + yDir]!!)
        }
        // up right
        if (!even && isWalkable(x + 1, y + yDir)) {
            neighbors.add(nodes[x + 1][y + yDir]!!)
        }
        // down right
        if (!even && isWalkable(x + 1, y - yDir)) {
            neighbors.add(nodes[x + 1][y - yDir]!!)
        }
        // down left
        if (even && isWalkable(x - 1, y - yDir)) {
            neighbors.add(nodes[x - 1][y - yDir]!!)
        }

        return neighbors
    }

    override fun getMovementCost(node1: Cell, node2: Cell, opt: PathFinderOptions): Float {
        if (node1 == node2)
            return 0f

        val options = opt as GridFinderOptions
        return if (node1.x == node2.x || node1.y == node2.y)
            options.orthogonalMovementCost
        else
            options.diagonalMovementCost
    }

    override fun isWalkable(node: Cell): Boolean {
        return isWalkable(node.x, node.y)
    }

    override fun getNodes(): Array<Array<Cell?>> {
        return nodes
    }

    override fun setNodes(nodes: Array<Array<Cell?>>) {
        this.width = nodes.size
        this.height = nodes[0].size

        this.nodes = nodes
    }

    override fun getWidth(): Int {
        return width
    }

    override fun setWidth(width: Int) {
        this.width = width
    }

    override fun getHeight(): Int {
        return height
    }

    override fun setHeight(height: Int) {
        this.height = height
    }

    override fun lineOfSight(from: NavigationNode?, to: NavigationNode?): Boolean {
        if (from == null || to == null)
            return false

        val node = from as NavigationGridGraphNode?
        val neigh = to as NavigationGridGraphNode?
        var x1 = node!!.x
        var y1 = node.y
        val x2 = neigh!!.x
        val y2 = neigh.y
        val dx = Math.abs(x1 - x2)
        val dy = Math.abs(y1 - y2)
        val xinc = if (x1 < x2) 1 else -1
        val yinc = if (y1 < y2) 1 else -1

        var error = dx - dy

        for (n in dx + dy downTo 1) {
            if (!isWalkable(x1, y1))
                return false
            val e2 = 2 * error
            if (e2 > -dy) {
                error -= dy
                x1 += xinc
            }
            if (e2 < dx) {
                error += dx
                y1 += yinc
            }
        }

        return true

    }
}
