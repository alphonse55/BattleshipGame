import java.util.Scanner;

public class Battle {

    public static void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e){e.printStackTrace();}
    }

    public static void clear_console(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static int[] convert_position(String position){
        String letters = "ABCDEFGHIJ";
        int x = letters.indexOf(position.charAt(0));
        int y;
        if (position.length() == 2){
            y = Integer.parseInt(String.valueOf(position.charAt(1))) - 1;
        }else{
            y = 9;
        }
        int[] a = {x, y};
        return a;
    }
    
    public static boolean ship_around(int x, int y, boolean[][] mat){
        if (y != 0)
            if (mat[y-1][x])
                return true;
        if (y != mat.length-1)
            if (mat[y+1][x])
                return true;
        if (x != 0)
            if (mat[y][x-1])
                return true;
        if (x != mat.length-1)
            if (mat[y][x+1])
                return true;
        if (mat[y][x])
            return true;
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[] ship_lengths = {2, 3, 3, 4, 5};
        String[] ship_names = {"Patrol Boat", "Submarine", "Destroyer", "Battleship", "Carrier"};
        int num_ships = ship_lengths.length;
        Ship[] ships = new Ship[num_ships];

        int grid_size = 10;
        boolean[][] field = new boolean[grid_size][grid_size];
        for (int j = 0; j < grid_size; j++){
            for (int i = 0; i < grid_size; i++){
                field[j][i] = false;
            }
        }

        for (int s = 0; s < num_ships; s++){

            int x = -1;
            int y = -1;
            int orientation = 0;

            boolean ship_done = false;
            while (!ship_done){
                orientation = (int) (Math.random() + 0.5); // 0 or 1; 0 -> x, 1 -> y
                if (orientation == 0){
                    x = (int) (Math.random() * (grid_size - ship_lengths[s]));
                    y = (int) (Math.random() * (grid_size));
                    
                    ship_done = true;
                    for (int i = x; i < x + ship_lengths[s]; i++){
                        if (ship_around(i, y, field)){
                            ship_done = false;
                        }
                    }
                }
                else if (orientation == 1){
                    x = (int) (Math.random() * (grid_size));
                    y = (int) (Math.random() * (grid_size - ship_lengths[s]));

                    ship_done = true;
                    for (int i = y; i < y + ship_lengths[s]; i++){
                        if (ship_around(x, i, field)){
                            ship_done = false;
                        }
                    }
                }
            }

            ships[s] = new Ship(field, ship_lengths[s], x, y, orientation, ship_names[s]);
        }
        Field computer_field = new Field(field);
        computer_field.print();

        // player field : 
        // boat 1 (length 2) : - vertical ? - starting point ?, if too low or too far right, ask again

        for (int j = 0; j < grid_size; j++){
            for (int i = 0; i < grid_size; i++){
                field[j][i] = false;
            }
        }

        Field player_field = new Field(field);

        ships = new Ship[num_ships];

        for (int s = 0; s < num_ships; s++){
            System.out.println();
            player_field.print();
            System.out.println();
            System.out.println(ship_names[s] + " (length = " + ship_lengths[s] + ") : ");

            int x = -1;
            int y = -1;
            int orientation = -1;

            boolean ship_done = false;
            while (!ship_done){
                // get orientation
                orientation = -1;
                while (true){
                    System.out.print("Orientation (x -> 0 / y -> 1) : ");
                    orientation = scanner.nextInt();
                    if (orientation == 0 || orientation == 1){
                        break;
                    }
                }
                // get position
                String position = "";
                scanner.nextLine();
                while (true){
                    System.out.print("Position (row letter - column number) ex.: A1, C6 : ");
                    position = scanner.nextLine();
                    if (("ABCDEFGHIJ".indexOf(position.charAt(0)) > -1) && ("123456789".indexOf(position.charAt(1)) > -1)){
                        if (position.length() == 2){
                            break;
                        }
                        else if (position.length() == 3){
                            if (("123456789".indexOf(position.charAt(1)) == 0) && (String.valueOf(position.charAt(2)).equals("0"))){
                                break;
                            }
                        }
                        else{
                            System.out.println("Invalid input, try again.");
                        }
                    }
                }
                int[] position_array = convert_position(position);
                x = position_array[0];
                y = position_array[1];
                
                ship_done = true;
                if (orientation == 0){
                    if (x > grid_size - ship_lengths[s]){
                        ship_done = false;
                        System.out.println("Too far right.");
                    }
                    else{
                        for (int i = x; i < x + ship_lengths[s]; i++){
                            if (ship_around(i, y, field)){
                                ship_done = false;
                                System.out.println("Too close to other ships.");
                                break;
                            }
                        }
                    }
                }
                else if (orientation == 1){
                    if (y > grid_size - ship_lengths[s]){
                        ship_done = false;
                        System.out.println("Too low.");
                    }
                    else{
                        for (int i = y; i < y + ship_lengths[s]; i++){
                            if (ship_around(x, i, field)){
                                ship_done = false;
                                System.out.println("Too close to other ships.");
                                break;
                            }
                        }
                    }
                }
            }
            ships[s] = new Ship(field, ship_lengths[s], x, y, orientation, ship_names[s]);
        }

        player_field.print();

        scanner.close();
    }
}