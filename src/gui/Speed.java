package gui;

/**
 * CLASSE SPEED : modelise un thread qui calcule la vitesse de telechargement
 * classe a part permettant de ne pas ralentir le chargement
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class Speed extends Thread {
	private Download d;
	private boolean loading, stop;
	private double av1, av2, speed;

	/**
	 * Constructeur de la classe
	 * @param d : chargement dont la vitesse est calculee
	 */
	public Speed(Download d) {
		this.d = d;
		this.loading = false;
		this.loading = false;
	}

	/**
	 * methode run du thread calcule la vitesse de download (byte / s)
	 */
	public void run() {
		while (!stop) {
			while (loading) {
				av1 = d.getAvancement();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				av2 = d.getAvancement();
				speed = (double) ((double) ((av2 - av1) / 100))
						* ((double) d.getSizeFile());
			}
		}
	}

	/**
	 * 
	 * @return la vitesse en byte par seconde
	 */
	public double getDownloadSpeed() {
		return speed;
	}

	/**
	 * set du boolean permettant a la boucle de s'executer
	 * @param l : 
	 */
	public void setLoading(boolean l) {
		this.loading = l;
	}
	
	/**
	 * methode qui permet d'arreter completement le thread
	 */
	public void stopIt() {
		this.loading = false;
		this.stop = true;
	}
}
