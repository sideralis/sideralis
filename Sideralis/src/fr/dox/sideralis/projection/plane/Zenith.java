package fr.dox.sideralis.projection.plane;

public class Zenith {
	private double rot;

	public Zenith(double rot) {
		this.rot = rot;
	}

	public void setRot(double rot) {
		this.rot = rot;
	}

	/**
	 * 
	 * @param az
	 * @param hau
	 * @return
	 */
	public double getX(double az, double hau) {
		double distL;
		double x;

		distL = 1 - hau / (Math.PI / 2);
		x = -distL * Math.cos(az + rot); 										// To have west on east and vice versa

		return x;
	}

	/**
	 * 
	 * @param az
	 * @param hau
	 * @return
	 */
	public double getY(double az, double hau) {
		double distL;
		double y;

		distL = 1 - hau / (Math.PI / 2);
		y = distL * Math.sin(az + rot);

		return y;
	}

}
