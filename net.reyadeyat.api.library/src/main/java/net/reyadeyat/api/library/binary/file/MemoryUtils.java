package net.reyadeyat.api.library.binary.file;

public class MemoryUtils {
    
    public static byte[] IntegerArrayToByteArray(int[] integer_array) {
        byte[] byte_array = new byte[integer_array.length*Integer.BYTES];
        for (int index = 0; index < integer_array.length; index++) {
            int integer = integer_array[index];
            byte_array[index * 4] = (byte) (integer >> 24);
            byte_array[index * 4 + 1] = (byte) (integer >> 16);
            byte_array[index * 4 + 2] = (byte) (integer >> 8);
            byte_array[index * 4 + 3] = (byte) integer;
        }
        return byte_array;
    }
    
    public static int[] ByteArrayToIntegerArray(byte[] byte_array) throws Exception {
        if (byte_array.length % Integer.BYTES != 0) {
            throw new Exception("Byte Array is not a " + Integer.BYTES + " bytes integer aligned array!");
        }
        int[] integer_array = new int[byte_array.length/Integer.BYTES];
        for (int index = 0; index < integer_array.length; index++) {
            int integer = 0;
            integer = (int)(((byte)byte_array[index * 4] & 0x000000FF) << 24)
                    | (int)(((byte)byte_array[index * 4 + 1] & 0x000000FF) << 16)
                    | (int)(((byte)byte_array[index * 4 + 2] & 0x000000FF) << 8)
                    | (int)((byte)byte_array[index * 4 + 3] & 0x000000FF);
            integer_array[index] = integer;
        }
        return integer_array;
    }
    
    public static void setIntegerAt(byte[] byte_array, int integer, int index) throws Exception {
        if (byte_array.length < index + Integer.BYTES) {
            throw new Exception("Insufficient bytes in the array to pack integer.");
        }
        
        byte_array[index] = (byte) (integer >> 24);
        byte_array[index + 1] = (byte) (integer >> 16);
        byte_array[index + 2] = (byte) (integer >> 8);
        byte_array[index + 3] = (byte) integer;
    }
    
    public static int getIntegerAt(byte[] byte_array, int index) throws Exception {
        if (byte_array.length < index + Integer.BYTES) {
            throw new Exception("Insufficient bytes in the array to convert to integer.");
        }
        int integer = (int)(((byte)byte_array[index] & 0x000000FF) << 24)
                | (int)(((byte)byte_array[index + 1] & 0x000000FF) << 16)
                | (int)(((byte)byte_array[index + 2] & 0x000000FF) << 8)
                | (int)((byte)byte_array[index + 3] & 0x000000FF);
        return integer;
    }
    
    public static void setLongAt(byte[] byte_array, long lonG, int index) throws Exception {
        if (byte_array.length < index + Long.BYTES) {
            throw new Exception("Insufficient bytes in the array to pack long.");
        }
        
        byte_array[index] = (byte) (lonG >> 56);
        byte_array[index + 1] = (byte) (lonG >> 48);
        byte_array[index + 2] = (byte) (lonG >> 40);
        byte_array[index + 3] = (byte) (lonG >> 32);
        byte_array[index + 4] = (byte) (lonG >> 24);
        byte_array[index + 5] = (byte) (lonG >> 16);
        byte_array[index + 6] = (byte) (lonG >> 8);
        byte_array[index + 7] = (byte) lonG;
    }
    
    public static long getLongAt(byte[] byte_array, int index) throws Exception {
        if (byte_array.length < index + Long.BYTES) {
            throw new Exception("Insufficient bytes in the array to convert to long.");
        }
        long lonG = (long)(((byte)byte_array[index] & 0x00000000000000FFL) << 56)
                | (long)(((byte)byte_array[index + 1] & 0x00000000000000FFL) << 48)
                | (long)(((byte)byte_array[index + 2] & 0x00000000000000FFL) << 40)
                | (long)(((byte)byte_array[index + 3] & 0x00000000000000FFL) << 32)
                | (long)(((byte)byte_array[index + 4] & 0x00000000000000FFL) << 24)
                | (long)(((byte)byte_array[index + 5] & 0x00000000000000FFL) << 16)
                | (long)(((byte)byte_array[index + 6] & 0x00000000000000FFL) << 8)
                | (long)((byte)byte_array[index + 7] & 0x00000000000000FFL);
        return lonG;
    }
    
    /*public static void main(String[] args) {
        try {
            Random random = new Random();
            {
                int int_array_size = random.nextInt(10) + 1; 
                int[] integer_array = new int[int_array_size];
                for (int index = 0; index < int_array_size; index++) {
                    integer_array[index] = (random.nextInt(1000000000) + 1);
                    integer_array[index] *= (integer_array[index] % 2 == 0 ? 1 : -1);
                    System.out.print(integer_array[index] + ", ");
                }
                System.out.println();
                byte[] byte_array = MemoryUtils.IntegerArrayToByteArray(integer_array);
                integer_array = MemoryUtils.ByteArrayToIntegerArray(byte_array);
                for (int index = 0; index < int_array_size; index++) {
                    System.out.print(integer_array[index] + ", ");
                }
            }
            System.out.println("------------------------------------------------------------------------");
            {
                //int int_array_size = random.nextInt(10) + 1; 
                int solts = 5;
                int byte_array_size = solts*(Long.BYTES+Integer.BYTES);
                byte[] byte_array = new byte[byte_array_size];
                for (int i = 0; i < solts; i++) {
                    int index = i*(Long.BYTES+Integer.BYTES);
                    int integer = 2000000000 + i;
                    long lonG = 4000000000L + i;
                    MemoryUtils.setIntegerAt(byte_array, integer, index+Long.BYTES);
                    MemoryUtils.setLongAt(byte_array, lonG, index);
                    System.out.print("["+lonG+","+integer+"],");
                }
                System.out.println();
                for (int i = 0; i < solts; i++) {
                    int index = i*(Long.BYTES+Integer.BYTES);
                    int integer = MemoryUtils.getIntegerAt(byte_array, index+Long.BYTES);
                    long lonG = MemoryUtils.getLongAt(byte_array, index);
                    System.out.print("["+lonG+","+integer+"],");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

}
