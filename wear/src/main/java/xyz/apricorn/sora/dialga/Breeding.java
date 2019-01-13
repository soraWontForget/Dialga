package xyz.apricorn.sora.dialga;

public class Breeding {

    private Boolean foreignParent;
    private Boolean shinyCharm;

    Breeding()
    {
        HeldItems heldItems = new HeldItems();
        foreignParent = true;
        shinyCharm = heldItems.getShinyCharm();
    }

    public Boolean getForeignParent()
    {
        return foreignParent;
    }

    public Boolean getShinyCharm()
    {
        return shinyCharm;
    }

}
