public class Vector2D {
    public float x;
    public float y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public Vector2D add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }

    public static Vector2D sub(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }

    public void normalize() {
        double mag = this.mag();
        this.x /= mag;
        this.y /= mag;
    }

    public double mag() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2)); 
    }

    public Vector2D mult(float scale) {
        this.x *= scale;
        this.y *= scale;
        return this;
    }
}
