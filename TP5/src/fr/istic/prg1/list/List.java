package fr.istic.prg1.list;


import fr.istic.prg1.list_util.Iterator;
import fr.istic.prg1.list_util.SuperT;

public class List<T extends SuperT> {
  private Element flag;
  
  private class Element {
    public T value;
    
    public Element left;
    
    public Element right;
    
    public Element() {
      this.value = null;
      this.left = null;
      this.right = null;
    }
    
    public Element(Element left, T v, Element right) {
      this.left = left;
      this.value = v;
      this.right = right;
    }
  }
  
  public class ListIterator implements Iterator<T> {
    private Element current;
    
    private ListIterator() {
        current = flag.right;
    }
    
    public void goForward() {
      this.current = this.current.right; //Élément à droite de l'élément courant 
    }
    
    public void goBackward() {
      this.current = this.current.left; //Élément à gauche de l'élément courant 
    }
    
    public void restart() {
      this.current = flag.right;  //On replace l'élément courant sur l'élément à droite du drapeau (1er élément de la liste)
    }
    
    public boolean isOnFlag() {
      return this.current == flag; //L'élément courant est-il le drapeau ?
    }
    
    public void remove() {
      try {
        assert this.current != List.this.flag : "\n\n\nimpossible de retirer le drapeau\n\n\n";
      } catch (AssertionError e) {
        e.printStackTrace();
        System.exit(0);
      } 
      Element voisinG = this.current.left;  //On stocke les voisins de gauche et de droite dans deux variables.
      Element voisinD = this.current.right;
      voisinG.right = voisinD; //On échange les valeurs
      voisinD.left = voisinG;
      this.current = voisinD; //Suppression de l'élément courant en le remplaçant par le voisin droite
    }
    
    public T getValue() {
      return this.current.value; 
    }
    
    public T nextValue() {
      return this.current.right.value; //Récupération de l'attribut valeur de l'élément de droite
    }
    
    public void addLeft(T v) {
      Element tmp = new Element(this.current.left, v, this.current);
      this.current.left.right = tmp;
      this.current.left = tmp;
      this.current = this.current.left;
    }
    
    public void addRight(T v) {
      Element tmp = new Element(this.current, v, this.current.right);
      this.current.right.left = tmp;
      this.current.right = tmp;
      this.current = this.current.right;
    }
    
    public void setValue(T v) {
      this.current.value = v;   
    }
    
    public void selfDestroy() { 
      this.current = null; //On fait pointer l'itérateur sur null
    }
    
    public String toString() {
      return "parcours de liste : pas d'affichage possible \n";
    }
  }
  
  public List() {
    flag = new Element(); //Création d'une liste avec un élément : le drapeau
    flag.left = flag;
    flag.right = flag;
  }
  
  public ListIterator iterator() { 
    ListIterator it = new ListIterator(); //Création d'un itérateur 
    it.current = flag.right;  //On place l'élément courant de l'itérateur sur le 1er élément de la liste
    return it;
  }
  
  public boolean isEmpty() {
    return this.flag == this.flag.right; //La liste contient uniquement le drapeau
  }
  
  public void clear() {
    this.flag.right = flag; //On supprime tous les autres éléments de la liste en ne gardant que le drapeau
    this.flag.left = flag;
  }
  
  public void setFlag(T v) {
    this.flag.value = v;  
  }
  
  public void addHead(T v) {
    ListIterator it = iterator();
    it.restart(); //On se place sur le premier élément 
    it.addLeft(v); //On ajoute v à gauche du 1er élément
  }
  
  public void addTail(T v) {
    ListIterator it = iterator();
    it.goBackward(); //On est sur le drapeau 
    it.addLeft(v);   //On ajoute à gauche du drapeau
  }
  
  public List<T> clone() {
    List<T> nouvListe = new List();
    ListIterator p = iterator();
    while (!p.isOnFlag()) {
      nouvListe.addTail((T)p.getValue().clone());
      p.goForward();
    } 
    return nouvListe;
  }
  
  public String toString() {
    String s = "contenu de la liste : \n";
    ListIterator p = iterator();
    while (!p.isOnFlag()) {
      s = String.valueOf(s) + p.getValue().toString() + " ";
      p.goForward();
    } 
    return s;
  }
  public static void main(String[] args) {
	  
 }

}
