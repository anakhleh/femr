import sys
data = ''

if len(sys.argv) < 3:
    sys.exit('usage: tabstocomma path/to/tab/file path/to/comma/fil')

with open(sys.argv[1], 'r') as file:
    data = file.read().replace('\t', ';').replace(',', ' ')

with open(sys.argv[2], 'w+') as newfile:
    newfile.write(data)
