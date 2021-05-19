FROM mcr.microsoft.com/dotnet/aspnet:3.1
COPY ./netcoreapp3.1/ /home
RUN mkdir -p /home/Users/RP/Pictures/Skany/
ENTRYPOINT dotnet /home/ChatFileManagement.dll
EXPOSE 5000
