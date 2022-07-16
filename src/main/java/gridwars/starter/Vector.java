package gridwars.starter;

public class Vector {
    Double x = null;
    Double y = null;
    Double z = null;

    int size = 0;

    Vector(double x){
        size++;
        this.x = x;
    }

    Vector(double x, double y) {
        this(x);
        size++;
        this.y = y;
    }

    Vector(double x , double y, double z) {
        this(x,y);
        size++;
        this.z = z;
    }

    Double mag() {
        return Math.sqrt(skalar(this.x, this.x, this.y, this.y, this.z, this.z));
    }


    void normalize() {
        div(mag());
    }



    static Double skalar(Double x, Double x1, Double y, Double y1, Double z , Double z1) {
        double sum = 0;

        if(x != null && x1 != null) {
            sum += x*x1;
        }
        if(y != null && y1 != null) {
            sum += y*y1;
        }

        if(z != null && z1 != null) {
            sum += z*z1;
        }
        return sum;
    }

    Double skalar(Vector other){
        return skalar(this.x, other.x, this.y, other.y, this.z, other.y);
    }


    Double distance(Vector other) {
        return Math.sqrt(skalar(this.x, other.x, this.y, other.y, this.z, other.z));
    }

    Double distance(Double x, Double y, Double z) {
        return Math.sqrt(skalar(this.x, x, this.y, y, this.z, z));
    }


    void add(Vector other) {
        assert this.size == other.size;
        add(other.x, other.y, other.z);
    }

    void add(Double x,Double y,Double z) {
        if(this.x != null && x != null) {
            this.x += x;
        }
        if(this.y != null && y != null) {
            this.y += y;
        }
        if(this.z != null && z != null) {
            this.z += z;
        }
    }

    void add(double a) {
        add(a,a,a);
    }



    static Vector sub(Vector a, Vector b){
        assert a.size == b.size;
        Vector newly;
        if(a.size == 1){
            newly = new Vector(a.x);
        } else if(a.size == 2){
            newly = new Vector(a.x, a.y);
        } else {
            newly = new Vector(a.x, a.y, a.z);
        }
        newly.sub(b);
        return newly;
    }


    void sub(Vector other) {
        assert this.size == other.size;
        sub(other.x, other.y, other.z);
    }

    void sub(Double x,Double y,Double z) {
        if(this.x != null && x != null) {
            this.x -= x;
        }
        if(this.y != null && y != null) {
            this.y -= y;
        }
        if(this.z != null && z != null) {
            this.z -= z;
        }
    }

    void sub(double a) {
        add(a,a,a);
    }


    void mult(Double val) {
        if(this.x != null && val != null) {
            this.x *= val;
        }
        if(this.y != null && val != null) {
            this.y *= val;
        }
        if(this.z != null && val != null) {
            this.z *= val;
        }
    }

    void div(Double val) {
        assert val != 0;
        if(this.x != null) {
            this.x /= val;
        }
        if(this.y != null) {
            this.y /= val;
        }
        if(this.z != null) {
            this.z /= val;
        }
    }

    void invert(){
        mult((double) -1);
    }




    Double angle(Vector other){
        assert other.mag() > 0;
        assert this.mag() > 0;
        double skalar = skalar(other);
        double magnitudes = this.mag()  * other.mag();
        return Math.acos(skalar /magnitudes);
    }

    Double angleFromNormal(){
        return angle(new Vector(1, 0));
    }

    Double angleX() {
        return angle(new Vector(1,0));
    }

    Double angleY(){
        return angle(new Vector(0,1));
    }

}


