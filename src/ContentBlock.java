
public class ContentBlock extends Block {
    public int weight=100;
    private int link_num=0;
    private int empty_row;
    private int longst_row;
    public int getlongst_row()
    {
	return longst_row;
    }
    public void setlongst_row(int a)
    {
	this.longst_row=a;
    }
    public int getemptyrow()
    {
	return empty_row;
    }
    public void setemptyrow(int a)
    {
	this.empty_row=a;
    }
    public int getweight()
    {
    	return weight;
    }
    public void setweight(int a){
    	this.weight=a;
    }
    public int getlink()
    {
	return link_num;
    }
    public void setlink(int a)
    {
	this.link_num=a;
    }
}
