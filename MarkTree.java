class MarkTree
{
    static class Mark
    {
	public int id;
	public String description;
	public String link;
	public long dateCreated;
	
    }

    static class MarkCategory
    {
	public Map<String, MarkCategory> children;
	public List<Mark> marks;
	
	public MarkCategory()
	{
	    children = new HashMap<String, MarkCategory>();
	    marks = new ArrayList<Mark>;
	}
    }

    MarkCategory root;
    
    public MarkTree()
    {
	root= new MarkCategory();
    }
}
