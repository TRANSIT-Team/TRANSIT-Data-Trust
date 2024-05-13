# Validation Strategy (ahve to be rewrite, sometiems there it work other)

- the rights service is only accessible over the backend
- association with  (change) object-attribute-operations rights cannot be changed
- association without (change) object-attribute-operations rights  cannot get this rights

### Create Objekt in Backend and create Rights in Graph

##### Existing Nodes
- PC (Policy Class)
- UA#1 (Node of Company) 

##### Creating Nodes
- OA#1 (Order Attribute, which store the accessible properties of the backend object)
- O#1 (Object Node--> should be a classType property inside?)

#### Parts of creation and validation
1. Create Nodes
   1. O#1
      1. No Validation by creating O#1 Node (possible check if classType property is in it?)
   2. OA#1 
      1. Validate if OA#1 node have read and writeProperties property in it
2. Create Connections
   1. Fixed order (Have to check every time)
      1. Create association form UA#1 node to OA#1 node with (read, write) object-operations and (change) object-attribute-operations on it
      2. Create assignment from OA#1 to PC and O to OA#1 node
         1. Cannot be done when there is no ingoing association on OA#1 node
         2. 

### UA#1 share rights on object 0#1 to company UA#2


##### Existing Nodes
- PC (Policy Class)
- UA#1 (Node of Company, which has rights) 
- UA#2 (Node of Company, which get rights) 
- OA#1 (Order Attribute)
- O#1 (Object Node)

##### Creating Nodes
- OA#2 (Order Attribute)
  

#### Parts of creation and validation
1. Create nodes
   1. OA#2
      1. Validate if OA#2 node have read and writeProperties property in it
2. Create Connections
   1. Fixes order (Have to check every time)
      1. Create RightsConnection from OA#2 to OA#1 
         1. Validate if read and writeProperties are not more then in parent node
            1. Check if the exists no other outgoing RightsConnection from OA#2
      2. Create association UA#1 to OA#2
         1. Validate the edge has only  (change) object-attribute-operations rights
            1. Cannot check if RightsConnection has been created
      3. Create Association UA#2 to OA#2
         1. Validate the edge has no (change) object-attribute-operations rights, other rights automatically checked by OA#2 node properties
            1. Can check if there exists another association on OA#2. Then validation above
      4. Create Assigment OA#2 to PC
         1. Always possible
      5. Create Assigment O#1 to OA#2
         1. When there is an outgoing assignment form 0#1 then have to be a RightsConnection have to be exists outgoing from OA#2


### UA#1 change rights on object 0#1 to company UA#2

- only OA nodes can be change?
- only associations can be change?

##### Existing Nodes
- PC (Policy Class)
- UA#1 (Node of Company, which has rights) 
- UA#2 (Node of Company, which get rights) 
- OA#1 (Order Attribute)
- O#1 (Object Node)
- OA#2 (Order Attribute)

##### Changing Nodes and Connections
- OA#2
- UA#2 to OA#2 (possible)
  
#### Parts of creation and validation
1. Change node
   1. Find OA#2 over intersect access nodes from UA#1 and UA#2
   2. Check if UA#1 has  (change) object-attribute-operations rights on OA#2
   3. Find the super-parent node on which OA#1 has access
      1. have to check that this node are not a node where UA#1 has no connected association
   4. Calculate intersection of properties, so that UA#2 get no more access then UA#1 on O#1
   5. Possible: Change Association from UA#2 to OA#2 when there is a changing in read and write properties

2. Change Connections
   1. Find OA#2 over intersect access nodes from UA#1 and UA#2
   2. Check if UA#1 has  (change) object-attribute-operations rights on OA#2
   3. Find the super-parent node on which OA#1 has access
      1. have to check that this node are not a node where UA#1 has no connected association
   4. Calculate intersection of object-operations rights, so that UA#2 get no more access then UA#1 on O#1






