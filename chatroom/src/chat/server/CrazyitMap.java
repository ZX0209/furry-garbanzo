package chat.server;

import java.util.*;

/**
 * 重写 352cb23j05
 *
 * @param <K>
 * @param <V>
 */
public class CrazyitMap<K, V> extends HashMap<K, V> {
    // 根据value来删除指定项
    public void removeByValue(Object value) {
        K k = this.getKeyByValue((V) value);
        if (k != null) {
            this.remove(k);
        }
    }

    // 根据value查找key
    public K getKeyByValue(V value) {
        K k = null;
        Set<Entry<K, V>> entries = this.entrySet();
        for (Entry<K, V> entry : entries) {
            V v = entry.getValue();
            if (v.equals(value) && v.hashCode() == value.hashCode()) {
                k = entry.getKey();
                break;
            }
        }
        return k;
    }

    // 重写HashMap的put方法，该方法不允许value重复
    @Override
    public V put(K key, V value) {
        //获取到所有的value
        Collection<V> values = this.values();
        //再添加k,v的时候,要去看
        for (V v : values) {
            //对象相等，用 equals() 和 hashCode()
            if (v.equals(value) && v.hashCode() == value.hashCode()) {
                throw new RuntimeException("MyMap实例中不允许有重复value!");
            }
        }
        return super.put(key, value);
    }


    public static void main(String[] args) {
        CrazyitMap<String, String> map = new CrazyitMap<>();
        map.put("name", "zs");
        map.put(null, "20");

        map.removeByValue("zs1");

    }
}
