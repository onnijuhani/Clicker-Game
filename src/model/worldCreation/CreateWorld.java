package model.worldCreation;

import static model.NameCreation.generateWorldName;

public class CreateWorld {
    private final Size size;
    private World world;
    private Quarter spawnQuarter;

    public CreateWorld() {
        this.size = Size.MEDIUM;
        this.world = new World(generateWorldName(), this.size);
        this.spawnQuarter = spawn();
    }

    private Quarter spawn() {
        Quarter spawn = this.world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0);
        return spawn;
    }

    public Quarter getSpawnQuarter() {
        return spawnQuarter;
    }
}
