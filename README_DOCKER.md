# Docker command for production
Only port 8080 for spring is mapped

docker-compose up --build

# Docker command for dev
All ports are mapped to:
- sharingAPP: 8080
- sharingDB: 5432
- propay: 8888 

docker-compose -f docker-compose-dev.yml up --build