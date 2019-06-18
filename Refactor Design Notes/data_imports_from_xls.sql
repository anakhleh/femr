/*
This SQL file contains a script to take the NDA medication product and package
databases and convert it into two tables usable for femr, one which details
a set of medications for femr, the other which details the packages medications
come in, more for scanning in a medication, or entering it via an NDC code.

Set paths to your package.xls and product.xls manually. Note that even on windows,
you still use the forward slash unix path seperator '/'. Tried to use local variables
that you could set at the top of the file, but LOAD DATA INTO will not take the file path
as a variable, only as a text literal.

AS OF 2019-06-18:
get tab-deliniated .xls database files from https://www.accessdata.fda.gov/cder/ndcxls.zip
Note, it's important you get the xls files instead of the txt files since the 
txt ones do not contain all the medications.

product.xls details medications themselves, not any way they are sold.
package.xls details the package itself (how it is sold)
and makes reference to the actual product contained

This sql script tries to do mostly one thing at a time rather than combining steps. Sorry for length.
S
*/

#Turn of safe mode for update statements without WHERE clauses and other ALTER TABLE statements.
SET SQL_SAFE_UPDATES = 0;
set SQL_SELECT_LIMIT = 10000000;

use femr;

#Import raw data from product.xls
drop table if exists medication_product;

create table medication_product (
PRODUCTID text,
PRODUCTNDC text,
PRODUCTTYPENAME text not null,
PROPRIETARYNAME text not null,
PROPRIETARYNAMESUFFIX text not null,
NONPROPRIETARYNAME text not null,
DOSAGEFORMNAME text not null,
ROUTENAME text not null,
STARTMARKETINGDATE text not null,
ENDMARKETINGDATE text not null,
MARKETINGCATEGORYNAME text not null,
APPLICATIONNUMBER text not null,
LABELERNAME text not null,
SUBSTANCENAME text not null,
ACTIVE_NUMERATOR_STRENGTH text not null,
ACTIVE_INGRED_UNIT text not null,
PHARM_CLASSES text not null,
DEASCHEDULE text not null,
NDC_EXCLUDE_FLAG text not null,
LISTING_RECORD_CERTIFIED_THROUGH text not null
);

load data infile 'C:/User Data/Software/femr/Refactor Design Notes/ndcxls1/product.xls'
into table medication_product
ignore 1 lines; -- first line as of 2019-06-18 was a line detailing the columns of the database

#Remove duplicate records with the same PRODUCTNDC.
alter table medication_product
drop PRODUCTTYPENAME, 
drop PROPRIETARYNAMESUFFIX,
drop ROUTENAME,
drop STARTMARKETINGDATE,
drop ENDMARKETINGDATE,
drop MARKETINGCATEGORYNAME,
drop APPLICATIONNUMBER, 
drop LABELERNAME,
drop SUBSTANCENAME,
drop PHARM_CLASSES,
drop DEASCHEDULE,
drop NDC_EXCLUDE_FLAG,
drop LISTING_RECORD_CERTIFIED_THROUGH;

update medication_product set PRODUCTID = trim(PRODUCTID);
update medication_product set PRODUCTNDC = trim(PRODUCTNDC);
update medication_product set PROPRIETARYNAME = trim(PROPRIETARYNAME);
update medication_product set NONPROPRIETARYNAME = trim(NONPROPRIETARYNAME);
update medication_product set DOSAGEFORMNAME = trim(DOSAGEFORMNAME);
update medication_product set ACTIVE_NUMERATOR_STRENGTH = trim(ACTIVE_NUMERATOR_STRENGTH);
update medication_product set ACTIVE_INGRED_UNIT = trim(ACTIVE_INGRED_UNIT);

alter table medication_product
change `PRODUCTID` `product_id` text,
change `PRODUCTNDC` `product_NDC` text,
change `PROPRIETARYNAME` `proprietary_name` text,
change `NONPROPRIETARYNAME` `nonproprietary_name` text,
change `DOSAGEFORMNAME` `dosage_form_name` text,
change `ACTIVE_NUMERATOR_STRENGTH` `active_numerator_strength` text,
change `ACTIVE_INGRED_UNIT` `active_ingredient_unit` text;

#Import raw data from package.xls
drop table if exists medication_package;

create table medication_package(
PRODUCTID text,
PRODUCTNDC text,
NDCPACKAGECODE text,
PACKAGEDESCRIPTION text,
STARTMARKETINGDATE text,
ENDMARKETINGDATE text,
NDC_EXCLUDE_FLAG text,
SAMPLE_PACKAGE text
);

load data infile 'C:/User Data/Software/femr/Refactor Design Notes/ndcxls1/package.xls'
into table medication_package
ignore 1 lines; -- first line as of 2019-06-18 was a line detailing the columns of the database

alter table medication_package
drop STARTMARKETINGDATE,
drop ENDMARKETINGDATE,
drop NDC_EXCLUDE_FLAG,
drop SAMPLE_PACKAGE;

update medication_package set PACKAGEDESCRIPTION = substring_index(PACKAGEDESCRIPTION, ' ', 1);
alter table medication_package 
change `PACKAGEDESCRIPTION` `quantity_in_package` text,
change `PRODUCTID` `product_id` text,
change `PRODUCTNDC` `product_ndc` text,
change `NDCPACKAGECODE` `ndc_package_code` text;


/*


#reduce the arity of table medication_package_raw in a lossy way
drop table if exists medication_package_arity_reduced;

create table medication_package_arity_reduced as
(
	select NDCPACKAGECODE, PRODUCTNDC, PACKAGEDESCRIPTION
	from medication_package_raw
);

#reduce the arity of table medication_product_raw in a lossy way
drop table if exists medication_product_arity_reduced;

create table medication_product_arity_reduced as
(
	select PRODUCTNDC, PROPRIETARYNAME, ACTIVE_NUMERATOR_STRENGTH, ACTIVE_INGRED_UNIT,
			NONPROPRIETARYNAME, DOSAGEFORMNAME
	from medication_product_raw
);

#For table medication_package_arity_reduced, the PACKAGEDESCRIPTION column contains the quantity
#in the package as the first word (space deliniated). Alter the column name, datatype and contents
#to show just this content.
update medication_package_arity_reduced 
set PACKAGEDESCRIPTION = substring_index(PACKAGEDESCRIPTION, ' ', 1);

alter table medication_package_arity_reduced
change column `PACKAGEDESCRIPTION` `QUANTITYINPACKAGE` INT(11);

#NOTE, For table medication_product_arity_reduced would have been changed ACTIVE_NUMERATOR_STRENGTH to an int
#but some medication products contain combinations of various medications formulated as XX;YY, so it is keept
#as a text field. In table medication_product_arity_reduced in the ACTIVE_INGRED_UNIT there are some values that are a bit
#unfriendly to read such as mg/1 and so forth. Did not convert because there are so many forms, and I do not have 
#the requisite knowledge to convert most of these properly.

*/

