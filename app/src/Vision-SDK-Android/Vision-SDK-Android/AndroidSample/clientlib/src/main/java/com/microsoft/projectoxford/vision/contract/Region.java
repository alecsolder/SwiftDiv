package com.microsoft.projectoxford.vision.contract;

import java.util.List;

public class Region {
    public String boundingBox; //e.g. "boundingBox":"27, 33, 398, 51"

    public List<Line> lines;
}
