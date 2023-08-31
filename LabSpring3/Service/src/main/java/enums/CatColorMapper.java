package enums;

import entities.CatColor;

public class CatColorMapper {
    public static ServiceCatColor CatColorToServiceCatColor(CatColor cc){
        switch (cc){
            case Grey -> {return ServiceCatColor.Grey;}
            case Brown -> {return ServiceCatColor.Brown;}
            case Black -> {return ServiceCatColor.Black;}
            case White -> {return ServiceCatColor.White;}
            case Yellow -> {return ServiceCatColor.Yellow;}
        }

        return null;
    }


    public static CatColor ServiceCatColorToCatColor(ServiceCatColor scc){
        switch (scc){
            case Grey -> {return CatColor.Grey;}
            case Brown -> {return CatColor.Brown;}
            case Black -> {return CatColor.Black;}
            case White -> {return CatColor.White;}
            case Yellow -> {return CatColor.Yellow;}
        }

        return null;
    }
}
