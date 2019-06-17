import sys
import os
import re
from tqdm import tqdm

delim = ','
target_db = 'test'

if len(sys.argv) < 4:
    sys.exit('usages:\n'
    +'csv_to_sql.py path/to/file.csv path/to/new.sql targetDatabase\n'
    +'csv_to_sql.py path/to/file.csv path/to/new.sql targetDatabase delim')
elif len(sys.argv) == 5:
    target_db = sys.argv[3]
    delim = sys.argv[4]

table_name = os.path.basename(sys.argv[2].split('.')[0])

inputlines = ''
csvfile = open(sys.argv[1], 'r')
inputlines = csvfile.readlines()

columns = inputlines[0].split(delim)
columns[-1] = columns[-1].replace('\n', '')
columns_for_create_statement = [c + ' text,\n' for c in columns[:-1]]
columns_for_create_statement += ((columns[-1] + ' text\n'))
columns_for_create_statement = ''.join(columns_for_create_statement)


outputlines = f'USE {target_db};\n\n'
outputlines += f'CREATE TABLE IF NOT EXISTS {table_name}({columns_for_create_statement});\n\n'

if len(inputlines) > 1:
    columns_for_insert_statement = ['`' + col + '`' for col in columns]
    outputlines += f'INSERT INTO `{table_name}`('+ (',\n'.join(columns_for_insert_statement)) + ')\nVALUES\n'

    linecount = 0
    total = len(inputlines) - 1
    for line in tqdm(inputlines[1:linecount - (linecount % 40000)]):
        row_values = line.split(delim)
        row_values[-1] = row_values[-1].replace('\n','')
        row_values = ['\''+ r.replace("'", "''") + '\'' for r in row_values]
        row_values_string = ', '.join(row_values)
        linecount += 1

        if linecount % 40000 == 0:
            outputlines += f'({row_values_string});\n\n'
            sqlFile = open(os.path.basename(sys.argv[2]) + f'_{linecount}.sql', 'w')
            sqlFile.write(outputlines)
            outputlines = f'USE {target_db};\n\n' \
                          + f'CREATE TABLE IF NOT EXISTS {table_name}({columns_for_create_statement});\n\n' \
                          + f'INSERT INTO `{table_name}`('+ (',\n'.join(columns_for_insert_statement)) + ')\nVALUES\n'
        else:
            outputlines += f'({row_values_string}),\n'

    outputlines = f'USE {target_db};\n\n' \
                  + f'CREATE TABLE IF NOT EXISTS {table_name}({columns_for_create_statement});\n\n' \
                  + f'INSERT INTO `{table_name}`('+ (',\n'.join(columns_for_insert_statement)) + ')\nVALUES\n'

    print('AAA', linecount)
    if linecount < total:
        for line in tqdm(inputlines[linecount:]):
                row_values = line.split(delim)
                row_values[-1] = row_values[-1].replace('\n','')
                row_values = ['\''+ r.replace("'", "''") + '\'' for r in row_values]
                row_values_string = ', '.join(row_values)
                linecount += 1
                if linecount == total-1:
                    print('BBB')
                    outputlines += f'({row_values_string});\n\n'
                    sqlFile = open(os.path.basename(sys.argv[2]) + f'_{linecount}.sql', 'w')
                    sqlFile.write(outputlines)
                    outputlines = f'USE {target_db};\n\n' \
                                  + f'CREATE TABLE IF NOT EXISTS {table_name}({columns_for_create_statement});\n\n' \
                                  + f'INSERT INTO `{table_name}`('+ (',\n'.join(columns_for_insert_statement)) + ')\nVALUES\n'
                else:
                    outputlines += f'({row_values_string}),\n'
