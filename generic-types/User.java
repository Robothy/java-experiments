class User implements Comparable<User> {
	String name;
	
	public int compareTo(User other){
		return this.name.compareTo(other.name);
	}
}