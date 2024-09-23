-- Zulu neovim plugin

local function setup()
    vim.cmd([[
        autocmd BufRead,BufNewFile *.zulu set filetype=zulu
        autocmd Syntax zulu runtime! syntax/zulu.vim
    ]])
end

return {
    setup = setup,
}
