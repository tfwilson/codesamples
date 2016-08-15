/*
 * rbbst.h
 *
 * Date        Author    Notes
 * =====================================================
 * 2014-04-14  Kempe     Initial version
 * 2015-04-06  Redekopp  Updated formatting and removed
 *                         KeyExistsException
 */
#ifndef RBBST_H
#define RBBST_H

#include <iostream>
#include <exception>
#include <cstdlib>
#include "bst.h"

/* -----------------------------------------------------
 * Red-Black Nodes and Search Tree
 ------------------------------------------------------*/

enum Color {red, black};

template <class KeyType, class ValueType>
class RedBlackNode : public Node<KeyType, ValueType>
{
public:
  RedBlackNode (KeyType k, ValueType v, RedBlackNode<KeyType, ValueType> *p)
    : Node<KeyType, ValueType> (k, v, p)
    {color = red; }
  
  virtual ~RedBlackNode () {}
  
  Color getColor () const
    { return color; }
  
  void setColor (Color c)
    { color = c; }
  
  virtual RedBlackNode<KeyType, ValueType> *getParent () const
    { return (RedBlackNode<KeyType,ValueType>*) this->_parent; }
  
  virtual RedBlackNode<KeyType, ValueType> *getLeft () const
    { return (RedBlackNode<KeyType,ValueType>*) this->_left; }
  
  virtual RedBlackNode<KeyType, ValueType> *getRight () const
    { return (RedBlackNode<KeyType,ValueType>*) this->_right; }

  virtual RedBlackNode<KeyType, ValueType> *getGrand() const
  { return (RedBlackNode<KeyType, ValueType>*) this->getParent()->getParent();}

  virtual RedBlackNode<KeyType, ValueType> *getSibling() const
  {
    RedBlackNode<KeyType, ValueType>* temp = (RedBlackNode<KeyType,ValueType>*) this->getParent();
    if (temp->getLeft() == this)
    {
      return temp->getRight();
    }
    else
    {
      return temp->getLeft();
    }
  }

  virtual RedBlackNode<KeyType, ValueType> *getUncle() const
  {
    RedBlackNode<KeyType, ValueType>* temp = (RedBlackNode<KeyType,ValueType>*) this->getParent();
    if (temp->getParent() == NULL)
    {
      return temp->getParent();
    }
    temp = temp->getParent();
    if (temp->getLeft() == this->getParent())
    {
      return temp->getRight();
    }
    else
    {
      return temp->getLeft();
    }
  }
  
 protected:
  Color color;
};

/* -----------------------------------------------------
 * Red-Black Search Tree
 ------------------------------------------------------*/

template <class KeyType, class ValueType>
class RedBlackTree : public BinarySearchTree<KeyType, ValueType>
{
public:
 
  void insert (const std::pair<const KeyType, ValueType>& new_item)
  { 
    KeyType k = new_item.first;
    ValueType val = new_item.second;
    bool done = false;
    if (this->root == NULL)
    {
      RedBlackNode<KeyType, ValueType> *newnode = new RedBlackNode<KeyType, ValueType>(k, val, NULL);
      newnode->setColor(black);
      this->root = newnode;
    }
    else if (this->root->getKey() ==  k)
    {
      this->root->setValue(val);
    }
    else 
    {
      RedBlackNode<KeyType, ValueType>* node = (RedBlackNode<KeyType,ValueType>*) this->root;
      while (!done) // or node = null?
      {
        if (k < node->getKey())
        {
          if (node->getLeft() == NULL)
          {
            RedBlackNode<KeyType, ValueType> *newnode = new RedBlackNode<KeyType, ValueType>(k, val, node);
            node->setLeft(newnode);
            done = true;
            fixtree(newnode);
          }
          else if (node->getLeft()->getKey() == k)
          {
            node->getLeft()->setValue(val);
            node->setColor(red);
            done = true;
          }
          else { node = node->getLeft();}
        }
        else
        {
          if (node->getRight() == NULL) //segfault here
          {
            RedBlackNode<KeyType, ValueType> *newnode = new RedBlackNode<KeyType, ValueType>(k, val, node);
            node->setRight(newnode);
            done = true;
            fixtree(newnode);
          }
          else if (node->getRight()->getKey() == k)
          {
            node->getRight()->setValue(val);
            done = true;
          }
          else {node = node->getRight();}
        }
      }
    }
  }

void rbbstprint()
{
  rbbstprinthelper(static_cast<RedBlackNode<KeyType,ValueType>*>(this->root));
  std::cout << std::endl;
}

// check if its root
// case 1 

// while node->parent != null
// if parent is red and not root, 
//  check uncle  
// if uncle is red  --- -case 2
// --- node becomes grandparent
// if uncle is black case 3
// -- node becomes parent


  /* This one is yours to implement.
     It should insert the (key, value) pair to the tree, 
     making sure that it remains a valid Red-Black Tree.
     If the key is already in the tree
     (with a possibly different associated value),
     then it should overwrite the value with the new one.
     We strongly recommend defining suitable (private) helper functions. */


protected:

  void fixtree(RedBlackNode<KeyType, ValueType> *node)
  {
    while (node->getParent() != NULL)
    {
      if (node->getColor() == red && node->getParent()->getColor() == red)
      {
        if (node->getUncle() != NULL && node->getUncle()->getColor() == red)
        {
          case2(node); // red uncle case2
          node = node->getParent();
        }
        else if(node->getUncle() != NULL && node->getUncle()->getColor() == black)
        {
          case3(node); //black uncle case 3
        }
        else if(node->getUncle() == NULL)
        {
          case3(node); //no uncle case 3
        }
      }
      if (node->getParent() != NULL)
      {
        node = node->getParent();
      }
    }
    if (node->getParent() == NULL)
    {
      node->setColor(black); //set root black
      this->root = node;
    }
  }

  void case2(RedBlackNode<KeyType, ValueType> *node) //if uncle is red
  {
    node->getParent()->setColor(black); //recolor
    node->getUncle()->setColor(black);
    node->getGrand()->setColor(red);
  }
  void case3(RedBlackNode<KeyType, ValueType> *node) //if uncle is black
  {
    if (node->getParent()->getLeft() == node && node->getGrand()->getLeft() == node->getParent())
    {
      leftleft(node->getParent(), node->getGrand());
    }
    else if (node->getParent()->getRight() == node && node->getGrand()->getLeft() == node->getParent())
    {
      leftright(node->getParent(), node->getGrand(), node);
    }
    else if (node->getParent()->getRight() == node && node->getGrand()->getRight() == node->getParent())
    {
      rightright(node->getParent(), node->getGrand());
    }
    else if (node->getParent()->getLeft() == node && node->getGrand()->getRight() == node->getParent())
    {
      rightleft(node->getParent(), node->getGrand(), node);
    }

  }

  
  void leftleft(RedBlackNode<KeyType, ValueType> *p, RedBlackNode<KeyType, ValueType> *g)
  {
     //parent of p is now parent of g
    if (g->getParent() == NULL)
    {
      p->setColor(black);
      p->setParent(NULL);
      this->root = p;

    }
    else //could be null
    {
      p->setParent(g->getParent());
      if (g->getParent()->getLeft() == g) //left/right child of parent of g is now p
      {
        g->getParent()->setLeft(p);
      }
      else {g->getParent()->setRight(p);}
    }
      g->setParent(p);
      if (p->getRight() != NULL) 
      {
        g->setLeft(p->getRight());
      }
      else {g->setLeft(NULL);}

      if (g->getLeft() != NULL) //could be null
      {
        g->getLeft()->setParent(g);
      }
      p->setRight(g);
      //RECOLOR
      p->setColor(black);
      g->setColor(red);
  }
      
  void leftright(RedBlackNode<KeyType, ValueType> *p, RedBlackNode<KeyType, ValueType>* g, RedBlackNode<KeyType, ValueType> *c)
  {
    g->setLeft(c); //set poitners where they need to be
    c->setParent(g);
    p->setRight(c->getLeft());
    if (p->getRight() != NULL)
    {
      p->getRight()->setParent(p);
    }
    c->setLeft(p);
    p->setParent(c);
    leftleft(c, g); //rotate leftleft
  }
  void rightright(RedBlackNode<KeyType, ValueType> *p, RedBlackNode<KeyType, ValueType> *g)
  {
    p->setParent(g->getParent());  //parent of p is now parent of g
    if (g->getParent() == NULL)
    {
      this->root = p;
    }
    else //could be null
    {
      if (g->getParent()->getRight() == g) //left/right child of parent of g is now p
      {
        g->getParent()->setRight(p);
      }
      else {g->getParent()->setLeft(p);}
    }
      g->setParent(p);
      if (p->getLeft() != NULL) 
      {
        g->setRight(p->getLeft());
      }
      else {g->setRight(NULL);}
      if (g->getRight() != NULL) //could be null
      {
        g->getRight()->setParent(g);
      }
      p->setLeft(g);
      //RECOLOR
      p->setColor(black);
      g->setColor(red);
  }
  void rightleft(RedBlackNode<KeyType, ValueType> *p, RedBlackNode<KeyType, ValueType>* g, RedBlackNode<KeyType, ValueType> *c)
  {
    g->setRight(c); //set pointers to where they need to be
    c->setParent(g);
    p->setLeft(c->getRight());
    if (p->getLeft() != NULL) 
    {
      p->getLeft()->setParent(p);
    }
    c->setRight(p);
    p->setParent(c);
    rightright(c, g); //rightright rotation
  }
void rbbstprinthelper(RedBlackNode<KeyType, ValueType> *r)
{
  if (r != NULL)
  {
  std::cout << "[";
  rbbstprinthelper (r->getLeft());
  if(r->getColor() == red)
  {
    std::cout << " (" << r->getKey() << ", red) ";
  }
  else
  {
    std::cout << " (" << r->getKey() << ", black) ";
  }
  rbbstprinthelper (r->getRight());
  std::cout << "]";
  }
}

 };



#endif
