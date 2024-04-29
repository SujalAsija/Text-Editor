
import java.util.Scanner;

class Node<T> {
  T data;
  Node<T> prev;
  Node<T> next;

  Node(T data) {
    this.data = data;
    this.prev = null;
    this.next = null;
  }
}

class DoubllyLinkedList<T> {
 public static <T> Node<T> insertNodeBefore(Node<T> node, Node<T> current, Node<T> head) {
    node.next = current;
    if (current != null) {
      node.prev = current.prev;
    } else {
      node.prev = null;
    }
    if (node.prev != null) {
      node.prev.next = node;
    } else {
      head = node;
    }
    if (node.next != null)
      node.next.prev = node;
    
    return head;
  }

  public static <T> Node<T> insertNodeAfter(Node<T> node, Node<T> current, Node<T> head) {
    node.prev = current;
    if (current != null) {
      node.next = current.next;
    } else {
      node.next = head;
    }
    if (node.next != null)
      node.next.prev = node;
    if (node.prev != null) {
      node.prev.next = node;
    } else {
      head = node;
    }
    return head;
  }

  public static <T> Node<T> deletePrevNode(Node<T> node, Node<T> head) {
    if (node != null && node.prev != null) {
      node.prev = node.prev.prev;
      if (node.prev != null) {
        node.prev.next = node;
      } else {
        head = node;
      }
    }
    return head;
  }

  public static <T> Node<T> deleteNextNode(Node<T> node, Node<T> head) {
    if (node != null && node.next != null) {
      node.next = node.next.next;
      if (node.next != null) {
        node.next.prev = node;
      }
    } else if (node == null && head != null) {
      head = head.next;
      if (head != null)
        head.prev = null;
    }
    return head;
  }
}

public class TextEditor {
  private Node<Node<Character>> rowHead; 
  private Node<Node<Character>> currRow; 
  private Node<Character> cursor; 
  private int cursorPosition;

  public TextEditor() {
    cursorPosition = 0;
    cursor = new Node<Character>('|');
    currRow = new Node<Node<Character>>(cursor);
    rowHead = currRow;
  }

  public void insert(String text) {
    for (Character c : text.toCharArray()) {
      Node<Character> newNode = new Node<Character>(c);
      currRow.data = DoubllyLinkedList.<Character>insertNodeBefore(newNode, cursor, currRow.data);
      cursorPosition++;
    }
    displayText();
  }

  public void backspace() {
    if (cursorPosition > 0) {
      currRow.data = DoubllyLinkedList.<Character>deletePrevNode(cursor, currRow.data);
      cursorPosition--;
    } else if (currRow.prev != null) {
      moveToPrevRowEnd();
      cursor.next = currRow.next.data;
      rowHead = DoubllyLinkedList.<Node<Character>>deleteNextNode(currRow, rowHead);
    }
    displayText();
  }

  public void enter() {
    if (cursor.prev != null)
      cursor.prev.next = null;
    else
      currRow.data = null;
    cursor.prev = null;

    Node<Node<Character>> newRow = new Node<Node<Character>>(cursor);
    rowHead = DoubllyLinkedList.<Node<Character>>insertNodeAfter(newRow, currRow, rowHead);
    currRow = newRow;
    cursorPosition = 0;
    
    displayText();
  }

  public void moveLeft() {
    if (cursor.prev != null) {
      cursor.data = cursor.prev.data;
      cursor = cursor.prev;
      cursor.data = '|';
      cursorPosition--;
    } else if (currRow.prev != null) {
      moveToPrevRowEnd();
    }
    displayText();
  }

  public void moveRight() {
    if (cursor.next != null) {
      cursor.data = cursor.next.data;
      cursor = cursor.next;
      cursor.data = '|';
      cursorPosition++;
    } else if (currRow.next != null) {
      detachCursor();
      currRow = currRow.next;
      cursorPosition = attachCursor(0);
    }
    displayText();
  }

  public void moveUp() {
    if (currRow.prev != null) {
      detachCursor();
      currRow = currRow.prev;
      cursorPosition = attachCursor(cursorPosition);
    }
    displayText();
  }

  // Move cursor downwards
  public void moveDown() {
    if (currRow.next != null) {
      detachCursor();
      currRow = currRow.next;
      cursorPosition = attachCursor(cursorPosition);
    }
    displayText();
  }

  /* Move the cursor to the end of the previous row */
  public void moveToPrevRowEnd() {
    detachCursor();
    currRow = currRow.prev;
    cursorPosition = attachCursor(-1);
  }

  
  int attachCursor(int position) {
    int i = 0;
    Node<Character> current = position == 0 ? null : currRow.data;
    while ((position < 0 || i < position - 1) && current != null && current.next != null) {
      i++;
      current = current.next;
    }
    currRow.data = DoubllyLinkedList.<Character>insertNodeAfter(cursor, current, currRow.data);
    if (current == null)
      return 0;
    return i + 1;
  }

  void detachCursor() {
    if (cursor.prev != null)
      cursor.prev.next = cursor.next;
    else
      currRow.data = cursor.next;
    if (cursor.next != null)
      cursor.next.prev = cursor.prev;
    cursor.next = cursor.prev = null;
  }

  // Display current text
  public void displayText() {
    System.out.println("------------------------ Text Editor Start ------------------------");
    Node<Node<Character>> row = rowHead;
    while (row != null) {
      Node<Character> current = row.data;
      while (current != null) {
        System.out.print(current.data);
        current = current.next;
      }
      System.out.println();
      row = row.next;
    }
    System.out.println("------------------------- Text Editor End -------------------------");
  }

  public static void main(String[] args) {
    TextEditor editor = new TextEditor();
    Scanner scanner = new Scanner(System.in);
    Character command;

    while (true) {
      System.out.println("Enter command (I: Insert, B: Backspace, N: Enter, L: Left, R: Right, U: Up, D: Down): ");
      String input = scanner.nextLine().trim();
      command = input.length() > 0 ? input.charAt(0) : ' ';

      switch (command) {
        case 'I':
          System.out.println("Enter text to insert: ");
          String text = scanner.nextLine();
          editor.insert(text);
          break;
        case 'B':
          editor.backspace();
          break;
        case 'N':
          editor.enter();
          break;
        case 'L':
          editor.moveLeft();
          break;
        case 'R':
          editor.moveRight();
          break;
        case 'U':
          editor.moveUp();
          break;
        case 'D':
          editor.moveDown();
          break;
        default:
          System.out.println("Invalid command!");
      }
    }
  }
}