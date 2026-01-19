$file = "src\main\resources\templates\owner\view-vehicle.html"
$content = Get-Content $file -Raw

# Define the new select block with all options
$newSelect = @"
                                                <select class="form-control form-select" name="status">
                                                    <option th:selected="`${vehicle.status == 'AVAILABLE'}"
                                                        value="AVAILABLE">Available</option>
                                                    <option th:selected="`${vehicle.status == 'RENTED'}"
                                                        value="RENTED">Rented</option>
                                                    <option th:selected="`${vehicle.status == 'MAINTENANCE'}"
                                                        value="MAINTENANCE">Maintenance</option>
                                                    <option th:selected="`${vehicle.status == 'UNAVAILABLE'}"
                                                        value="UNAVAILABLE">Unavailable</option>
                                                </select>
"@

# Regex to match the existing select block
# We match from <select...name="status"> to </select>
# (?s) enables single-line mode so . matches newlines
$pattern = '(?s)<select class="form-control form-select" name="status">.*?</select>'

if ($content -match $pattern) {
    $content = $content -replace $pattern, $newSelect
    Set-Content $file $content -NoNewline
    Write-Host "Successfully updated status dropdown."
}
else {
    Write-Host "Pattern not found."
}
