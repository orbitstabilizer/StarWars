package project.sector;

import project.warships.Warship;

import java.util.*;

class Element{
    int power = 0;
    Warship warship = null;
    Element(int power, Warship warship){
        this.power = power;
        this.warship = warship;
    }
}

/**
 * Modified Segment Tree for handling the assault event
 */
public class AssaultHandler {
    private int size;
    private Element[] tree;
    private ArrayList<Warship> warships;
    private HashMap<Warship,Warship> attackMap;

    /**
     * complexity: O(n)
     * creates a segment tree of Separatist warships
     * @param warships
     */
    public AssaultHandler(ArrayList<Warship> warships) {
        this.warships = warships;
        size = 1;
        while (size < warships.size()) size *= 2;
        tree = new Element[2 * size];
        this.size = warships.size();
        attackMap = new HashMap<>();
        this.build(warships, 0, 0, size);

    }

    /**
     * complexity : O(1)
     * adds warship to target's potential attackers,
     * if the target is not in the attack map,then adds warship to the attack map as it is the only attacker
     * else if the target is in the attack map,
     *  if previous attacker's coordinate is less than the warship's coordinate, then replace the previous attacker with the warship
     *
     * @param target Separatist cruiser that is being targeted
     * @param warship Republican cruiser that targets
     */
    public void setTarget(Warship target, Warship warship){
        if (attackMap.get(target) == null) attackMap.put(target, warship);
        else if (attackMap.get(target).getCoordinate() < warship.getCoordinate()) attackMap.put(target, warship);
    }

    /**
     *
     * @return target-attacker map
     */
    public HashMap<Warship, Warship> getAttackMap() {
        return attackMap;
    }

    /**
     * complexity : O(log(n))
     * helper function for getTarget function
     * standard segment tree query
     * @param warship Republican cruiser that looks for a target
     * @return Separatist destroyer that is the target of the Republican cruiser / null if no target
     */
    public Warship getTarget(Warship warship){
        var tmp =  this.getTarget(warship.getPowerOutput(), 0, 0, size);
        if (tmp == null) return null;
        if (tmp.power < warship.getPowerOutput()){
            return tmp.warship;
        }
        return null;
    }

    /**
     * complexity : O(log(n))
     * helper function for check function
     * updates warship's power output to Integer.MAX_VALUE, this way it will be ignored in the next query
     * @param warship Separatist destroyer that is considered for an assault target
     */
    public void setVisited(Warship warship){
        var tmp = Collections.binarySearch(warships, warship, Comparator.comparing(Warship::getCoordinate));
        if (tmp < 0) return;
        this.setVisited(warship,tmp, 0, 0, size);

    }

    /**
     * complexity : O(n)
     * builds the segment tree
     * @param warships Separatist warships
     * @param x current index in the tree
     * @param lx left index of the current segment
     * @param rx right index of the current segment
     */
    private void build(ArrayList<Warship> warships, int x, int lx, int rx) {
        if (rx - lx == 1){
            if (lx<this.size){
                tree[x] = new Element(warships.get(lx).getPowerOutput(), warships.get(lx));
            }
            return;
        }
        int mid = (lx + rx) / 2;
        build(warships, 2 * x + 1, lx, mid);
        build(warships, 2 * x + 2, mid, rx);
        tree[x] = merge(tree[2 * x + 1], tree[2 * x + 2]);

    }

    /**
     * complexity : O(1)
     * @param a first element
     * @param b second element
     * @return element with the minimum power
     */
    private Element merge(Element a, Element b) {
        if (b == null) {
            return a;
        }
        if (a == null) {
            return b;
        }
        if (a.power > b.power) {
            return b;
        } else {
            return a;
        }
    }

    /**
     * complexity : O(log(n))
     * standard segment tree point update
     * @param warship Separatist destroyer that is considered for an assault target
     * @param index of the Separatist destroyer in the array
     * @param x current index in the tree
     * @param lx left index of the current segment
     * @param rx right index of the current segment
     */
    private void setVisited(Warship warship, int index, int x, int lx, int rx) {
        if (rx - lx == 1){
            if (lx<this.size){
                if (tree[x].warship.getCoordinate() == warship.getCoordinate()){
                    tree[x].power = Integer.MAX_VALUE;
                }
            }
            return;
        }
        int mid = (lx + rx) / 2;
        if (index < mid){
            setVisited(warship,index, 2 * x + 1, lx, mid);
        } else {
            setVisited(warship,index, 2 * x + 2, mid, rx);
        }
        tree[x] = merge(tree[2 * x + 1], tree[2 * x + 2]);

    }

    /**
     * complexity : O(log(n))
     * standard segment tree query
     * @param power power of the targeting Republican cruiser
     * @param x current index in the tree
     * @param lx left index of the current segment
     * @param rx right index of the current segment
     * @return target Separatist destroyer
     */
    private Element getTarget(int power, int x, int lx, int rx) {
        if (rx-lx == 1){
            return tree[x];
        }
        int mid = (lx + rx) / 2;
        if (power > tree[2 * x + 1].power) {
            return getTarget(power, 2 * x + 1, lx, mid);
        } else if (power > tree[2 * x + 2].power) {
            return getTarget(power, 2 * x + 2, mid, rx);
        }
        return null;
    }


}