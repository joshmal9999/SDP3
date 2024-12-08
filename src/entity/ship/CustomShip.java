package entity.ship;

import engine.DrawManager;
import entity.Ship;
import entity.ShipMultipliers;
import entity.ShipType;

public class CustomShip extends Ship {

    public CustomShip(final int positionX, final int positionY) {
        super(positionX, positionY,
              "CustomShip", new ShipMultipliers(1, 1, 1),
              DrawManager.SpriteType.CustomShip);
    }
}
