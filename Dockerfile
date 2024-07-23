FROM ghcr.io/cerbos/cerbos:0.34.0

# Set the working directory in the container
WORKDIR /cerbos-policies

# Copy policies from the host to the container
COPY ./cerbos-policies /policies

# Expose ports
EXPOSE 3592
EXPOSE 3593

# Command to compile policies
CMD ["compile", "/policies"]

