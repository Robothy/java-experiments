class User2 implements Comparable<User2> {
	String name;
	
	public int compareTo(User2 other){
		return this.name.compareTo(other.name);
	}
}