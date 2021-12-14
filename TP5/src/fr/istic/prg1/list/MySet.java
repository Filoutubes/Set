package fr.istic.prg1.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import fr.istic.prg1.list_util.Iterator;
//import fr.istic.prg1.list_util.List;
import fr.istic.prg1.list.List;
import fr.istic.prg1.list_util.Comparison;
import fr.istic.prg1.list_util.SmallSet;


/**
 * @author Mickael Foursov <foursov@univ-rennes1.fr>
 * @version 5.0
 * @since 2018-10-02
 */

public class MySet extends List<SubSet> {

	/**
	 * Borne superieure pour les rangs des sous-ensembles.
	 */
	private static final int MAX_RANG = 128;
	/**
	 * Sous-ensemble de rang maximal a mettre dans le drapeau de la liste.
	 */
	private static final SubSet FLAG_VALUE = new SubSet(MAX_RANG,
			new SmallSet());
	/**
	 * Entree standard.
	 */
	private static final Scanner standardInput = new Scanner(System.in);

	public MySet() {
		super();
		setFlag(FLAG_VALUE);
	}

	/**
	 * Fermer tout (actuellement juste l'entree standard).
	 */
	public static void closeAll() {
		standardInput.close();
	}

	private static Comparison compare(int a, int b) {
		if (a < b) {
			return Comparison.INF;
		} else if (a == b) {
			return Comparison.EGAL;
		} else {
			return Comparison.SUP;
		}
	}

	/**
	 * Afficher a l'ecran les entiers appartenant a this, dix entiers par ligne
	 * d'ecran.
	 */
	public void print() {
		System.out.println(" [version corrigee de contenu]");
		this.print(System.out);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// //////////// Appartenance, Ajout, Suppression, Cardinal
	// ////////////////////
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Ajouter a this toutes les valeurs saisies par l'utilisateur et afficher
	 * le nouveau contenu (arret par lecture de -1).
	 */
	public void add() {
		System.out.println(" valeurs a ajouter (-1 pour finir) : ");
		this.add(System.in);
		System.out.println(" nouveau contenu :");
		this.printNewState();
	}

	/**
	 * Ajouter a this toutes les valeurs prises dans is.
	 * C'est une fonction auxiliaire pour add() et restore().
	 * 
	 * @param is
	 *            flux d'entree.
	 */
	public void add(InputStream is) {
		Scanner sc = new Scanner(is);
		Iterator<SubSet> it = this.iterator();
		boolean fini = false;
		int nb;
		while(!fini) {
			it.restart();
			nb = readValue(sc,-2);
			if ( nb == -1 ) {
				fini = true;
			}
			else {
				while( ! it.isOnFlag() && it.getValue().rank < nb/256) {
					it.goForward();
				}
				switch(compare(it.getValue().rank , nb/256)) {
					case EGAL:   //Cas où le MySet est déjà présent dans la liste
						it.getValue().set.add(nb%256);
						break;
					default:
						it.addLeft(new SubSet(nb/256, new SmallSet()));
						it.getValue().set.add(nb%256);
						break;
				}
			}
		}
	}

	/**
	 * Ajouter value a this.
	 * 
	 * @param value
	 *            valuer a ajouter.
	 */
	public void addNumber(int value) {
		if( 0<=value && value<=32767) {
			Iterator<SubSet> it = this.iterator();
			while( !it.isOnFlag() && it.getValue().rank < value/256) {
				it.goForward();
			}
			switch(compare(it.getValue().rank , value/256)) {
				case EGAL:
					it.getValue().set.add(value%256);
					break;
				default:
					it.addLeft(new SubSet(value/256, new SmallSet()));
					it.getValue().set.add(value%256);
					break;
			}
		}
	}

	/**
	 * Supprimer de this toutes les valeurs saisies par l'utilisateur et
	 * afficher le nouveau contenu (arret par lecture de -1).
	 */
	public void remove() {
		System.out.println("  valeurs a supprimer (-1 pour finir) : ");
		this.remove(System.in);
		System.out.println(" nouveau contenu :");
		this.printNewState();
	}

	/**
	 * Supprimer de this toutes les valeurs prises dans is.
	 * 
	 * @param is
	 *            flux d'entree
	 */
	public void remove(InputStream is) {
		Scanner sc = new Scanner(is);
		Iterator<SubSet> it = this.iterator();
		boolean fini = false;
		int nb;
		while(!fini) {
			it.restart();
			nb = readValue(sc,-2);
			if ( nb == -1 ) {
				fini = true;
			}
			else {
				while( !it.isOnFlag() && it.getValue().rank < nb/256) {
					it.goForward();
				}
				switch(compare(it.getValue().rank , nb/256)) {
					case EGAL:
						it.getValue().set.remove(nb%256);
						if(it.getValue().set.isEmpty()) {
							it.remove();
						}
						break;
					default:
						break;
				}
			}
		}
	}

	/**
	 * Supprimer value de this.
	 * 
	 * @param value
	 *            valeur a supprimer
	 */
	public void removeNumber(int value) {
		if( 0<=value && value<=32767) {
			Iterator<SubSet> it = this.iterator();
			while( !it.isOnFlag() && it.getValue().rank < value/256) {
				it.goForward();
			}
			switch(compare(it.getValue().rank , value/256)) {
				case EGAL:
					it.getValue().set.remove(value%256);
					if(it.getValue().set.isEmpty()) {
						it.remove(); //Passe directement à l'élément suivant
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * @return taille de l'ensemble this
	 */
	public int size() {
		int count = 0;
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while (! it.isOnFlag()) {
			count+=it.getValue().set.size();
			it.goForward();
		}
		return count;
	}


	/**
	 * @return true si le nombre saisi par l'utilisateur appartient a this,
	 *         false sinon
	 */
	public boolean contains() {
		System.out.println(" valeur cherchee : ");
		int value = readValue(standardInput, 0);
		return this.contains(value);
	}

	/**
	 * @param value
	 *            valeur a tester
	 * @return true si valeur appartient a l'ensemble, false sinon
	 */

	public boolean contains(int value) {
		Iterator<SubSet> it = this.iterator();
		it.restart();
		while(!it.isOnFlag() && it.getValue().rank <= value/256) {
			if(it.getValue().rank == value/256 && it.getValue().set.contains(value%256)) {
				return true;
			}
			else {
				it.goForward();
			}
		}
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////// Difference, DifferenceSymetrique, Intersection, Union ///////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * This devient la difference de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void difference(MySet set2) {
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		if(this == set2) {
			this.clear(); //Si on a 2 objets identiques leur différence créé un objet vide
			return;
		}

		while(!it1.isOnFlag() && !it2.isOnFlag()) {
			switch(compare(it1.getValue().rank,it2.getValue().rank)) {
				case SUP:
					it2.goForward(); //si it1.getValue est > à it2.getValue, on fait avancer it2
					break;
				case INF:
					it1.goForward(); //et inversement
					break;
				case EGAL:
					it1.getValue().set.difference(it2.getValue().set);
					if(it1.getValue().set.isEmpty()) {
						it1.remove();
					}
					else {
						it1.goForward();
					}
					it2.goForward();
			}
		}
	}

	/**
	 * This devient la difference symetrique de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void symmetricDifference(MySet set2) {
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		if(this == set2) {
			this.clear();
			return;
		}

		while(!it1.isOnFlag() || !it2.isOnFlag()) {
			switch(compare(it1.getValue().rank,it2.getValue().rank)) {
				case SUP:
					it1.addLeft(new SubSet(it2.getValue().rank , it2.getValue().set.clone())); //add left car le rank du MySet à ajouter est + petit que celui sur lequel it1 se trouve. 
					if(!it1.isOnFlag()) { it1.goForward(); }					// On clone la valeur de it2 car on ne veut pas modifier set2
					it2.goForward();
					break;
				case INF:
					it1.goForward();
					break;
				case EGAL:
					it1.getValue().set.symmetricDifference(it2.getValue().set);
					if(it1.getValue().set.isEmpty()) {
						it1.remove();
					}
					else {
						it1.goForward();
					}
					it2.goForward();
					break;
			}
		}
	}

	/**
	 * This devient l'intersection de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void intersection(MySet set2) {
		
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();
		while(!it1.isOnFlag()) {
			
			switch(compare(it1.getValue().rank,it2.getValue().rank)) {
				case SUP:
					it2.goForward();
					break;
				case INF:
					it1.remove();
					break;
				case EGAL:
					it1.getValue().set.intersection(it2.getValue().set);
					if(it1.getValue().set.isEmpty()) {
						it1.remove();
					}
					else {
						it1.goForward();
					}
					it2.goForward();
					break;
			}
		}
	}

	/**
	 * This devient l'union de this et set2.
	 * 
	 * @param set2
	 *            deuxieme ensemble
	 */
	public void union(MySet set2) {
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		while(!it1.isOnFlag() || !it2.isOnFlag()) {
			switch(compare(it1.getValue().rank,it2.getValue().rank)) {
				case SUP:
					it1.addLeft(new SubSet(it2.getValue().rank,it2.getValue().set.clone()));
					if(!it1.isOnFlag()) { it1.goForward(); }
					it2.goForward();
					break;
				case INF:
					it1.goForward();
					break;
				case EGAL:
					it1.getValue().set.union(it2.getValue().set);
					it1.goForward(); //On fait bien avancer les 2 itérateurs
					it2.goForward();
					break;
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////////
	// /////////////////// Egalite, Inclusion ////////////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * @param o
	 *            deuxieme ensemble
	 * 
	 * @return true si les ensembles this et o sont egaux, false sinon
	 */
	@SuppressWarnings("unchecked") // car on vÈrifie auparavant si o n'est pas instance de MySet
	@Override
	public boolean equals(Object o) {
		boolean b = true;
		if (this == o) {
			b = true;
		} else if (o == null) {
			b = false;
		} else if (!(o instanceof MySet)) {
			b = false;
		} else {
			Iterator<SubSet> it1 = this.iterator();
			Iterator<SubSet> it2 = ((List<SubSet>) o).iterator();
			while((!it1.isOnFlag() || !it2.isOnFlag()) && b) { // on rajoute la cond b pour pouvoir s'arrÍter dËs qu'on trouve la moindre diffÈrence entre les deux sets
				switch(compare(it1.getValue().rank,it2.getValue().rank)) {
					case EGAL:
						if(!it1.getValue().set.equals(it2.getValue().set)) {
							b = false;
						}
						break;
					default:
						b=false;
						break;
				}
				if(!it1.isOnFlag()) { it1.goForward(); }
				it2.goForward();
			}
			/*List<SubSet> this_clone = this.clone();
			this.union((MySet) o);
			b = (this_clone.equals(this));*/
		}
		return b;
	}

	/**
	 * @param set2
	 *            deuxieme ensemble
	 * @return true si this est inclus dans set2, false sinon
	 */
	public boolean isIncludedIn(MySet set2) {
		Iterator<SubSet> it1 = this.iterator();
		Iterator<SubSet> it2 = set2.iterator();

		while(!it1.isOnFlag()) {
			switch(compare(it1.getValue().rank,it2.getValue().rank)) {
				case SUP:
					it2.goForward();
					break;
				case INF:
					/*
					 * on a pas d'inclusion (car des éléments de this ne sont pas dans set2) 
					 */
					return false;
				case EGAL:
					if(!it1.getValue().set.isIncludedIn(it2.getValue().set)) {
						return false;
					}
					// sinon, on a bien l'inclusion
					it1.goForward();
					it2.goForward();
					break;
			}
		}

		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// //////// Rangs, Restauration, Sauvegarde, Affichage //////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Afficher les rangs presents dans this.
	 */
	public void printRanks() {
		System.out.println(" [version corrigee de rangs]");
		this.printRanksAux();
	}

	private void printRanksAux() {
		int count = 0;
		System.out.println(" Rangs presents :");
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			System.out.print("" + it.getValue().rank + "  ");
			count = count + 1;
			if (count == 10) {
				System.out.println();
				count = 0;
			}
			it.goForward();
		}
		if (count > 0) {
			System.out.println();
		}
	}

	/**
	 * Creer this a partir d'un fichier choisi par l'utilisateur contenant une
	 * sequence d'entiers positifs terminee par -1 (cf f0.ens, f1.ens, f2.ens,
	 * f3.ens et f4.ens).
	 */
	public void restore() {
		String fileName = readFileName();
		InputStream inFile;
		try {
			inFile = new FileInputStream(fileName);
			System.out.println(" [version corrigee de restauration]");
			this.clear();
			this.add(inFile);
			inFile.close();
			System.out.println(" nouveau contenu :");
			this.printNewState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("fichier " + fileName + " inexistant");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier " + fileName);
		}
	}

	/**
	 * Sauvegarder this dans un fichier d'entiers positifs termine par -1.
	 */
	public void save() {
		System.out.println(" [version corrigee de sauvegarde]");
		OutputStream outFile;
		try {
			outFile = new FileOutputStream(readFileName());
			this.print(outFile);
			outFile.write("-1\n".getBytes());
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("pb ouverture fichier lors de la sauvegarde");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier");
		}
	}

	/**
	 * @return l'ensemble this sous forme de chaine de caracteres.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		SubSet subSet;
		int startValue;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			subSet = it.getValue();
			startValue = subSet.rank * 256;
			for (int i = 0; i < 256; ++i) {
				if (subSet.set.contains(i)) {
					String number = String.valueOf(startValue + i);
					int numberLength = number.length();
					for (int j = 6; j > numberLength; --j) {
						number += " ";
					}
					result.append(number);
					++count;
					if (count == 10) {
						result.append("\n");
						count = 0;
					}
				}
			}
			it.goForward();
		}
		if (count > 0) {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Imprimer this dans outFile.
	 * 
	 * @param outFile
	 *            flux de sortie
	 */
	private void print(OutputStream outFile) {
		try {
			String string = this.toString();
			outFile.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Afficher l'ensemble avec sa taille et les rangs presents.
	 */
	private void printNewState() {
		this.print(System.out);
		System.out.println(" Nombre d'elements : " + this.size());
		this.printRanksAux();
	}

	/**
	 * @param scanner
	 * @param min
	 *            valeur minimale possible
	 * @return l'entier lu au clavier (doit Ítre entre min et 32767)
	 */
	private static int readValue(Scanner scanner, int min) {
		int value = scanner.nextInt();
		while (value < min || value > 32767) {
			System.out.println("valeur incorrecte");
			value = scanner.nextInt();
		}
		return value;
	}

	/**
	 * @return nom de fichier saisi psar l'utilisateur
	 */
	private static String readFileName() {
		System.out.print(" nom du fichier : ");
		String fileName = standardInput.next();
		return fileName;
	}
}