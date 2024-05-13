
CREATE OR REPLACE FUNCTION GoogleEncodePolygon
(
  g1 geometry
)
RETURNS text AS
$BODY$
DECLARE
 ng INT;        -- Store number of Geometries in the Polygon.
 g INT;         -- Counter for the current geometry number during outer loop.
 g2 GEOMETRY;   -- Current geometry feature isolated by the outer loop.
 nr INT;        -- Store number of internal ring parts in the Polygon.
 r INT;         -- Counter for the current inner-ring part.
 r1 GEOMETRY;   -- Exterior ring part isolated BEFORE the inner loop.
 r2 GEOMETRY;   -- Inner-ring part isolated within the inner loop.
 gEncoded TEXT; -- Completed Google Encoding.
BEGIN
 gEncoded = '';
 ng = ST_NumGeometries(g1);
 g = 1;
 if(ng>1) then 
    FOR g IN 1..ng BY 1 LOOP
        g2 = ST_GeometryN(g1, g);

        if g > 1 then gEncoded = gEncoded || chr(8224); END IF;

        r1 = ST_ExteriorRing(g2);
        gEncoded = gEncoded || ST_AsEncodedPolyline(r1,5);
        nr = ST_NRings(g2);
        if nr > 1 then


        FOR r IN 1..(nr-1) BY 1 LOOP
            r2 = ST_InteriorRingN(g2, r);

            gEncoded = gEncoded || chr(8225) || ST_AsEncodedPolyline(r2,5);
        END LOOP;
        END IF;
    END LOOP;
 END IF;
 RETURN gEncoded;
End
$BODY$
  LANGUAGE plpgsql;