package com.builderline.whereisit;

public class Geo {

    private float lat = 0;
    private float lng = 0;
    private String coordsString = "";

    public Geo(float lat, float lng, String coordsString){
        this.coordsString = coordsString;
        this.lat = lat;
        this.lng = lng;
    }

    public Geo(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public String getCoordsString(){
        return coordsString;
    }

    public static Geo calcFromGeographicToDeс(String[] coords) {
        double a_deg_gr = Float.parseFloat(coords[0]);
        double a_deg_m = Float.parseFloat(coords[1]);
        double a_deg_s = Float.parseFloat(coords[2]);
        float x = (float) ((a_deg_gr * Math.PI / 180 + a_deg_m * Math.PI / (180 * 60) + a_deg_s * Math.PI / (
                        180 * 60 * 60)) * 180 / Math.PI);

        a_deg_gr = Float.parseFloat(coords[3]);
        a_deg_m = Float.parseFloat(coords[4]);
        a_deg_s = Float.parseFloat(coords[5]);
        float y = (float) ((a_deg_gr * Math.PI / 180 + a_deg_m * Math.PI / (180 * 60) + a_deg_s * Math.PI / (
                        180 * 60 * 60)) * 180 / Math.PI);

        return new Geo(x, y);

    }

    public static Geo calcFromRectToDeс(String[] coords){
        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);

        float lat = (float) mxtograd(x, y);
        float lng = (float) mytograd(x, y);
        return new Geo(lat, lng);

    }


    private static double mxtograd(double a, double b) {
        double betapx = a / 6367558.4968;
        double sin2B = Math.sin(betapx) * Math.sin(betapx);
        double sin4B = sin2B * sin2B;
        double B0x = betapx + Math.sin(2 * betapx) * (.00252588685 - 1.49186E-5 * sin2B + 1.1904E-7 * sin4B);
        double z0x = (b - 1E5 * (10 * Math.floor(1E-6 * b) + 5)) / (6378245 * Math.cos(B0x));
        double sin2B0 = Math.sin(B0x) * Math.sin(B0x);
        double sin4B0 = sin2B0 * sin2B0;
        double sin6B0 = sin4B0 * sin2B0;
        double dBx = -z0x * z0x * Math.sin(2 * B0x) * (.251684631 - .003369263 * sin2B0 + 1.1276E-5 * sin4B0 - z0x * z0x * (
                .10500614 - .04559916 * sin2B0 + .00228901 * sin4B0 - 2.987E-5 * sin6B0 - z0x * z0x * (
                        .042858 - .025318 * sin2B0 + .01434 * sin4B0 - .001264 * sin6B0 - z0x * z0x * (
                                .01672 - .0063 * sin2B0 + .01188 * sin4B0 - .00328 * sin6B0))));
        return 180 * (B0x + dBx) / 3.14159265358979;
    }

    private static double mytograd (double a, double b){
        double betapy = a / 6367558.4968;
        double sin2B = Math.sin(betapy) * Math.sin(betapy);
        double sin4B = sin2B * sin2B;
        double B0y = betapy + Math.sin(2 * betapy) * (.00252588685 - 1.49186E-5 * sin2B + 1.1904E-7 * sin4B);
        double z0y = (b - 1E5 * (10 * Math.floor(1E-6 * b) + 5)) / (6378245 * Math.cos(B0y));
        double sin2B0 = Math.sin(B0y) * Math.sin(B0y);
        double sin4B0 = sin2B0 * sin2B0;
        double sin6B0 = sin4B0 * sin2B0;
        double ly = z0y * (1 - .0033467108 * sin2B0 - 5.6002E-6 * sin4B0 - 1.87E-8 * sin6B0 - z0y * z0y * (
                .16778975 + .16273586 * sin2B0 - 5.249E-4 * sin4B0 - 8.46E-6 * sin6B0 - z0y * z0y * (
                        .0420025 + .1487407 * sin2B0 + .005942 * sin4B0 - 1.5E-5 * sin6B0 - z0y * z0y * (
                                .01225 + .09477 * sin2B0 + .03282 * sin4B0 - 3.4E-4 * sin6B0 - z0y * z0y * (
                                        .0038 + .0524 * sin2B0 + .0482 * sin4B0 + .0032 * sin6B0)))));
        return 180 * (6 * (Math.floor(1E-6 * b) - .5) / 57.29577951 + ly) / 3.14159265358979;
    }

}
