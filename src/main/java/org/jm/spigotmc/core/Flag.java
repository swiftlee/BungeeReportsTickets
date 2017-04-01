package org.jm.spigotmc.core;

public enum Flag {

    OPEN, CLOSED, DUPLICATE, NEED_ADMIN;
    @Override
    public String toString(){
        if(this==OPEN){return "Open";}
        else if(this==CLOSED){return "Closed";}
        else if(this==DUPLICATE){return "Duplicate";}
        else if(this==NEED_ADMIN){return "Need Admin";}
        else{return name();}
    }

}
