
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class MarkTree
{
  public static final int JSON_PARSE_OK = 0;
  public static final int JSON_PARSE_NO_ROOT = 1;
  public static final int JSON_PARSE_NO_CHILDREN = 2;
  public static final int JSON_PARSE_NO_TYPE = 3;
  public static final int JSON_PARSE_INVALID_TYPE = 4;
  public static final int JSON_PARSE_NO_NAME = 5;
  public static final int JSON_PARSE_INVALID_ID = 6;
  public static final int JSON_PARSE_INVALID_DATE = 7;

  static class Mark
  {
    public int id;
    public String description;
    public String link;
    public long dateAdded;

    public int fromJSON(JSONObject jnode)
    {
      Object node;
      node = jnode.get("id");
      if (!(node instanceof Number))
        return JSON_PARSE_INVALID_ID;
      id = ((Number) node).intValue();
      node = jnode.get("date_added");
      if (!(node instanceof Number))
        return JSON_PARSE_INVALID_DATE;
      dateAdded = ((Number) node).longValue();
      link = jnode.get("url").toString();
      description = jnode.get("name").toString();
      return JSON_PARSE_OK;
    }
  }

  static class MarkCategory
  {
    public Map<String, MarkCategory> children;
    public List<Mark> marks;

    public MarkCategory()
    {
      children = new HashMap<String, MarkCategory>();
      marks = new ArrayList<Mark>();
    }

    public int fromJSON(JSONObject jnode)
    {
      return fromJSON(jnode, true);
    }

    public int fromJSON(JSONObject jnode, boolean clear)
    {
      Map<String, MarkCategory> newChildren;
      List<Mark> newMarks;
      Object node;
      JSONObject jchild;
      String type, name;
      JSONArray jchildren;
      MarkCategory category;
      Mark mark;
      int ret;

      if (clear) {
        newChildren = new HashMap<String, MarkCategory>();
        newMarks = new ArrayList<Mark>();
      } else {
        newChildren = children;
        newMarks = marks;
      }
      node = jnode.get("children");
      if (node == null || !(node instanceof JSONArray))
        return JSON_PARSE_NO_CHILDREN;
      jchildren = (JSONArray) node;
      for (Object child : jchildren) {
        if (!(child instanceof JSONObject))
          continue;
        jchild = (JSONObject) child;
        node = jchild.get("type");
        if (node == null || !(node instanceof String))
          return JSON_PARSE_NO_TYPE;
        type = (String) node;
        if (type.equals("folder")) {
          node = jchild.get("name");
          if (node == null || !(node instanceof String))
            return JSON_PARSE_NO_NAME;
          name = (String) node;
          category = new MarkCategory();
          ret = category.fromJSON(jchild);
          if (ret != JSON_PARSE_OK)
            return ret;
          children.put(name.toLowerCase(), category);
        } else if (type.equals("url")) {
          mark = new Mark();
          ret = mark.fromJSON(jchild);
          if (ret != JSON_PARSE_OK)
            return ret;
          marks.add(mark);
        } else
          return JSON_PARSE_INVALID_TYPE;
      }
      children = newChildren;
      marks = newMarks;
      return JSON_PARSE_OK;
    }
  }

  public MarkCategory roots;

  public MarkTree()
  {
    roots = new MarkCategory();
  }

  public int fromJSON(JSONObject top)
  {
    MarkCategory newRoots;
    Object node;
    JSONObject jnode;
    int ret;
    node = top.get("roots");
    if (node == null || !(node instanceof JSONObject))
      return JSON_PARSE_NO_ROOT;
    newRoots = new MarkCategory();
    jnode = (JSONObject) node;
    for (Object child : jnode.entrySet()) {
      if (!(child instanceof JSONObject))
        continue;
      ret = newRoots.fromJSON((JSONObject) child, false);
      if (ret != JSON_PARSE_OK)
        return ret;
    }
    roots = newRoots;
    return JSON_PARSE_OK;
  }
}
